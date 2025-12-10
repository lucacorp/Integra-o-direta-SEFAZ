# Integração Direta SEFAZ - NF-e Brasil 🇧🇷

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)

Biblioteca Java para integração **direta** com webservices da SEFAZ para emissão de NF-e (Nota Fiscal Eletrônica), **sem necessidade do ACBrMonitor**.

## 🎯 Por que usar esta biblioteca?

- ✅ **Gratuita e Open Source** - Sem custos de licenciamento
- ✅ **Comunicação Direta HTTPS/SOAP** - Elimina dependências de ACBrMonitor
- ✅ **Certificado Digital A1** - Suporte nativo a certificados .pfx/.p12
- ✅ **Assinatura Digital XML** - Implementação completa do padrão NFe
- ✅ **Múltiplos Estados** - SP, MG, RJ e SVRS (demais estados)
- ✅ **Ambientes Homologação/Produção** - Fácil alternância
- ✅ **Testes Automatizados** - 27 testes unitários e de integração
- ✅ **Spring Boot Ready** - Integração nativa com Spring

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Certificado Digital A1 (.pfx ou .p12)
- Spring Boot 3.2+ (opcional, mas recomendado)

## 🚀 Instalação

### Maven

Adicione as dependências no seu `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>3.2.4</version>
    </dependency>

    <!-- Apache HttpClient 5 para HTTPS/SOAP -->
    <dependency>
        <groupId>org.apache.httpcomponents.client5</groupId>
        <artifactId>httpclient5-fluent</artifactId>
        <version>5.3.1</version>
    </dependency>

    <!-- BouncyCastle para criptografia -->
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcprov-jdk18on</artifactId>
        <version>1.78.1</version>
    </dependency>
    
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcpkix-jdk18on</artifactId>
        <version>1.78.1</version>
    </dependency>

    <!-- Lombok (opcional) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 📖 Como Usar

### 1. Configurar Certificado Digital

```java
@Configuration
public class NfeConfig {
    
    @Bean
    public CertificadoDigital certificadoDigital() throws Exception {
        CertificadoDigital cert = new CertificadoDigital();
        cert.carregar(
            "C:/certificados/seu_certificado.pfx",
            "SUA_SENHA"
        );
        return cert;
    }
}
```

### 2. Assinar XML da NF-e

```java
@Service
@RequiredArgsConstructor
public class NfeService {
    
    private final AssinaturaDigital assinaturaDigital;
    
    public String emitirNFe(String xmlNFe) throws Exception {
        // Assina o XML
        String xmlAssinado = assinaturaDigital.assinar(xmlNFe);
        
        return xmlAssinado;
    }
}
```

### 3. Enviar para SEFAZ

```java
@Service
@RequiredArgsConstructor
public class NfeService {
    
    private final SefazSoapClient sefazClient;
    
    public String enviarParaSefaz(String xmlAssinado, String uf) throws Exception {
        // Obtém URL do webservice
        String url = SefazEndpoints.getUrlAutorizacao(uf, true); // true = homologação
        
        // Envia para SEFAZ
        String respostaSefaz = sefazClient.enviarNFe(xmlAssinado, url);
        
        // Extrai informações da resposta
        String codigoStatus = sefazClient.extrairCodigoStatus(respostaSefaz);
        String mensagem = sefazClient.extrairMensagem(respostaSefaz);
        
        if ("100".equals(codigoStatus)) {
            System.out.println("NF-e autorizada com sucesso!");
        }
        
        return respostaSefaz;
    }
}
```

### 4. Consultar Recibo

```java
public String consultarRecibo(String numeroRecibo, String uf) throws Exception {
    String url = SefazEndpoints.getUrlConsultaProtocolo(uf, true);
    
    String resposta = sefazClient.consultarRecibo(numeroRecibo, url);
    
    String codigoStatus = sefazClient.extrairCodigoStatus(resposta);
    String mensagem = sefazClient.extrairMensagem(resposta);
    
    return resposta;
}
```

## 🏗️ Arquitetura

### Componentes Principais

| Classe | Responsabilidade |
|--------|-----------------|
| `CertificadoDigital` | Gerencia certificado A1 (.pfx/.p12) |
| `AssinaturaDigital` | Assinatura XML usando RSA-SHA1 |
| `SefazSoapClient` | Cliente HTTPS/SOAP para SEFAZ |
| `SefazEndpoints` | URLs dos webservices por UF |

### Fluxo de Emissão

```
┌─────────────────┐
│  Gerar XML NFe  │
└────────┬────────┘
         │
         ▼
┌─────────────────────┐
│  Assinar XML        │
│  (AssinaturaDigital)│
└────────┬────────────┘
         │
         ▼
┌──────────────────────┐
│  Enviar HTTPS/SOAP   │
│  (SefazSoapClient)   │
└────────┬─────────────┘
         │
         ▼
┌──────────────────────┐
│  Processar Resposta  │
│  cStat / xMotivo     │
└──────────────────────┘
```

## 🧪 Testes

Execute os testes:

```bash
mvn test
```

**Cobertura de Testes:**
- `AssinaturaDigitalTest` - 4 testes (assinatura XML)
- `SefazSoapClientTest` - 8 testes (parsing de respostas)
- `SefazEndpointsTest` - 8 testes (URLs por UF)
- `NfeIntegrationTest` - 10 testes (integração completa)

## 🌎 Estados Suportados

| UF | Webservice | Ambiente |
|----|-----------|----------|
| SP | Fazenda SP | Homologação/Produção |
| MG | Fazenda MG | Homologação/Produção |
| RJ | Fazenda RJ | Homologação/Produção |
| Outros | SVRS | Homologação/Produção |

## ⚙️ Configuração

### application.properties

```properties
# Certificado Digital
nfe.certificado.caminho=C:/certificados/certificado.pfx
nfe.certificado.senha=SENHA_CERTIFICADO

# Ambiente (true = homologação, false = produção)
nfe.homologacao=true

# Dados da Empresa
empresa.cnpj=00000000000000
empresa.razaoSocial=EMPRESA EXEMPLO LTDA
empresa.uf=SP
```

## 🔒 Segurança

### Boas Práticas

1. **Nunca commite certificados ou senhas** no repositório
2. Use **variáveis de ambiente** para dados sensíveis:
   ```bash
   export NFE_CERT_SENHA=sua_senha_aqui
   ```
3. Mantenha certificados em **diretório seguro** com permissões restritas
4. Use **certificados diferentes** para homologação e produção

### Exemplo com Variáveis de Ambiente

```java
@Value("${NFE_CERT_CAMINHO}")
private String certificadoCaminho;

@Value("${NFE_CERT_SENHA}")
private String certificadoSenha;
```

## 📚 Documentação SEFAZ

- [Manual de Integração NF-e](http://www.nfe.fazenda.gov.br/portal/principal.aspx)
- [Schemas XML NF-e 4.0](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=/fSAYUzYko=)
- [Códigos de Status SEFAZ](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=Iy7sqFgUZgM=)

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 🙏 Agradecimentos

Este projeto foi criado para ajudar a comunidade brasileira de desenvolvedores que precisam integrar com SEFAZ sem os custos do ACBrMonitor.

**Motivação:** O ACBr é uma ferramenta poderosa, mas o ACBrMonitor tem limitações na versão demo e custos de licenciamento. Esta biblioteca oferece uma alternativa open source e gratuita.

## ⚠️ Disclaimer

Esta biblioteca é fornecida "como está", sem garantias. Teste extensivamente em ambiente de **homologação** antes de usar em **produção**.

## 📞 Suporte

- Abra uma [Issue](https://github.com/lucacorp/Integracao-direta-SEFAZ/issues) para bugs ou dúvidas
- Contribua com código via Pull Requests
- Compartilhe com outros desenvolvedores!

---

**Desenvolvido com ❤️ pela comunidade brasileira de desenvolvedores**

**#OpenSource #NFe #SEFAZ #Brasil #Java #SpringBoot**
