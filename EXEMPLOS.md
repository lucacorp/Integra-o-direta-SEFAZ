# Exemplo de Uso - Integração SEFAZ NF-e

## Exemplo Completo de Emissão

```java
package com.exemplo.app;

import com.sefaz.nfe.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExemploEmissaoNFe {

    private final CertificadoDigital certificadoDigital;
    private final AssinaturaDigital assinaturaDigital;
    private final SefazSoapClient sefazClient;

    public void emitirNFe() {
        try {
            // 1. Carregar certificado (faça isso uma vez, no startup da aplicação)
            certificadoDigital.carregar(
                "C:/certificados/empresa.pfx",
                "senha123"
            );
            
            // 2. Gerar XML da NF-e (você precisa implementar isso)
            String xmlNFe = gerarXmlNFe();
            
            // 3. Assinar o XML
            String xmlAssinado = assinaturaDigital.assinar(xmlNFe);
            log.info("XML assinado com sucesso");
            
            // 4. Enviar para SEFAZ
            String uf = "SP";
            boolean homologacao = true;
            String url = SefazEndpoints.getUrlAutorizacao(uf, homologacao);
            
            String respostaSefaz = sefazClient.enviarNFe(xmlAssinado, url);
            
            // 5. Processar resposta
            String codigoStatus = sefazClient.extrairCodigoStatus(respostaSefaz);
            String mensagem = sefazClient.extrairMensagem(respostaSefaz);
            
            log.info("Status: {} - {}", codigoStatus, mensagem);
            
            if ("100".equals(codigoStatus)) {
                log.info("✓ NF-e autorizada com sucesso!");
            } else if ("103".equals(codigoStatus)) {
                // Lote recebido com sucesso, aguardar processamento
                String numeroRecibo = sefazClient.extrairNumeroRecibo(respostaSefaz);
                log.info("Recibo: {}", numeroRecibo);
                
                // 6. Consultar recibo após alguns segundos
                Thread.sleep(5000);
                consultarRecibo(numeroRecibo, uf, homologacao);
            } else {
                log.error("✗ Erro ao autorizar NF-e: {} - {}", codigoStatus, mensagem);
            }
            
        } catch (Exception e) {
            log.error("Erro ao emitir NF-e", e);
        }
    }
    
    private void consultarRecibo(String numeroRecibo, String uf, boolean homologacao) {
        try {
            String url = SefazEndpoints.getUrlConsultaProtocolo(uf, homologacao);
            
            String resposta = sefazClient.consultarRecibo(numeroRecibo, url);
            
            String codigoStatus = sefazClient.extrairCodigoStatus(resposta);
            String mensagem = sefazClient.extrairMensagem(resposta);
            
            log.info("Consulta recibo - Status: {} - {}", codigoStatus, mensagem);
            
            if ("100".equals(codigoStatus)) {
                log.info("✓ NF-e autorizada! Protocolo recebido.");
                // Extrair e salvar o protocolo
            }
        } catch (Exception e) {
            log.error("Erro ao consultar recibo", e);
        }
    }
    
    private String gerarXmlNFe() {
        // Implemente a geração do XML conforme seu modelo de negócio
        // Veja: http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=/fSAYUzYko=
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <NFe xmlns="http://www.portalfiscal.inf.br/nfe">
                <infNFe Id="NFe35240112345678000190650010000000011000000001" versao="4.00">
                    <ide>
                        <cUF>35</cUF>
                        <cNF>00000001</cNF>
                        <natOp>VENDA</natOp>
                        <mod>65</mod>
                        <serie>1</serie>
                        <nNF>1</nNF>
                        <dhEmi>2024-01-01T10:00:00-03:00</dhEmi>
                        <tpNF>1</tpNF>
                        <idDest>1</idDest>
                        <cMunFG>3550308</cMunFG>
                        <tpImp>4</tpImp>
                        <tpEmis>1</tpEmis>
                        <cDV>1</cDV>
                        <tpAmb>2</tpAmb>
                        <finNFe>1</finNFe>
                        <indFinal>1</indFinal>
                        <indPres>1</indPres>
                        <procEmi>0</procEmi>
                        <verProc>1.0</verProc>
                    </ide>
                    <emit>
                        <CNPJ>12345678000190</CNPJ>
                        <xNome>EMPRESA EXEMPLO LTDA</xNome>
                        <enderEmit>
                            <xLgr>RUA EXEMPLO</xLgr>
                            <nro>123</nro>
                            <xBairro>CENTRO</xBairro>
                            <cMun>3550308</cMun>
                            <xMun>SAO PAULO</xMun>
                            <UF>SP</UF>
                            <CEP>01000000</CEP>
                        </enderEmit>
                        <IE>123456789</IE>
                        <CRT>1</CRT>
                    </emit>
                    <!-- Adicione dest, det, total, transp, pag, infAdic conforme necessário -->
                </infNFe>
            </NFe>
            """;
    }
}
```

## Exemplo de Configuração Spring Boot

```java
package com.exemplo.app.config;

import com.sefaz.nfe.CertificadoDigital;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class NFeConfiguration {
    
    @Value("${nfe.certificado.caminho}")
    private String certificadoCaminho;
    
    @Value("${nfe.certificado.senha}")
    private String certificadoSenha;
    
    /**
     * Carrega o certificado digital no startup da aplicação
     */
    @Bean
    public ApplicationRunner certificadoLoader(CertificadoDigital certificadoDigital) {
        return args -> {
            try {
                log.info("Carregando certificado digital...");
                certificadoDigital.carregar(certificadoCaminho, certificadoSenha);
                log.info("✓ Certificado carregado com sucesso");
            } catch (Exception e) {
                log.error("✗ Erro ao carregar certificado digital", e);
                // Decida se quer interromper a inicialização ou continuar
                // throw new RuntimeException("Falha ao carregar certificado", e);
            }
        };
    }
}
```

## Exemplo application.properties

```properties
# NF-e - Certificado Digital
nfe.certificado.caminho=C:/certificados/empresa.pfx
nfe.certificado.senha=${NFE_CERT_PASSWORD}

# NF-e - Ambiente
nfe.homologacao=true

# Empresa
empresa.cnpj=12345678000190
empresa.razaoSocial=EMPRESA EXEMPLO LTDA
empresa.inscricaoEstadual=123456789
empresa.uf=SP
empresa.cidade=SAO PAULO
empresa.cep=01000-000
```

## Exemplo de Teste de Status da SEFAZ

```java
package com.exemplo.app;

import com.sefaz.nfe.SefazEndpoints;
import com.sefaz.nfe.SefazSoapClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TesteStatusSefaz {
    
    private final SefazSoapClient sefazClient;
    
    public void verificarStatus() {
        try {
            String uf = "SP";
            boolean homologacao = true;
            
            String url = SefazEndpoints.getUrlStatusServico(uf, homologacao);
            
            // Monta XML de consulta de status
            String xmlConsulta = """
                <?xml version="1.0" encoding="UTF-8"?>
                <consStatServ xmlns="http://www.portalfiscal.inf.br/nfe" versao="4.00">
                    <tpAmb>2</tpAmb>
                    <cUF>35</cUF>
                    <xServ>STATUS</xServ>
                </consStatServ>
                """;
            
            // Enviar (você precisará adaptar o SefazSoapClient para ter este método)
            // String resposta = sefazClient.consultarStatus(xmlConsulta, url);
            
            log.info("Status da SEFAZ verificado com sucesso");
            
        } catch (Exception e) {
            log.error("Erro ao verificar status SEFAZ", e);
        }
    }
}
```

## Códigos de Status SEFAZ Comuns

| Código | Descrição |
|--------|-----------|
| 100 | Autorizado o uso da NF-e |
| 101 | Cancelamento homologado |
| 102 | Inutilização homologada |
| 103 | Lote recebido com sucesso |
| 104 | Lote processado |
| 105 | Lote em processamento |
| 135 | Evento registrado e vinculado a NF-e |
| 217 | NF-e já está cancelada |
| 218 | NF-e já está inutilizada |
| 301 | Uso denegado |
| 302 | Uso denegado por irregularidade fiscal |
| 539 | CNPJ do emitente inválido |
| 540 | CPF do destinatário inválido |

Para lista completa, consulte: [http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=Iy7sqFgUZgM=](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=Iy7sqFgUZgM=)
