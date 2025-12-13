# DC-e - Declaração de Conteúdo Eletrônica

## 📦 Visão Geral

A **DC-e (Declaração de Conteúdo Eletrônica)** é um documento fiscal eletrônico utilizado pelos **Correios** para declaração de conteúdo de encomendas postadas, conforme especificação da SEFAZ.

### Características Principais

- **Modelo**: 59 (documento fiscal eletrônico)
- **Uso**: Declaração de conteúdo para encomendas postais
- **Destinatário**: Obrigatório (diferente da NFCe)
- **Autenticação**: Não utiliza QR Code nem CSC
- **Assinatura**: XMLDSig padrão ICP-Brasil
- **Disponibilidade**: Apenas 14 estados brasileiros

### Estados que Suportam DC-e

✅ **AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO**

❌ Não disponível nos demais estados

---

## 🏗️ Arquitetura

### Componentes

```
dce/
├── DceEndpoints.java      # URLs SEFAZ (homologação + produção) para 14 UFs
├── DceXmlBuilder.java     # Construção do XML modelo 59
├── DadosDCe.java         # Modelo de dados principal
└── ItemDCe.java          # Modelo de item da DC-e
```

### Dependências Necessárias

Esta implementação **NÃO** inclui:
- Cliente SOAP (você deve implementar ou usar biblioteca como Apache CXF, JAX-WS)
- Assinatura digital XMLDSig (use biblioteca de assinatura ICP-Brasil)
- Certificado digital A1 (você deve fornecer)
- Comunicação HTTPS com mTLS

**Recomendações**:
- Apache HttpClient 5.x para HTTPS
- BouncyCastle para criptografia
- XmlDSig para assinatura (veja exemplo no repositório `nfe/`)

---

## 🚀 Como Usar

### 1. Criar Dados da DC-e

```java
import br.gov.sefaz.dce.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Criar dados da DC-e
DadosDCe dados = new DadosDCe();
dados.setNumeroLote(1);
dados.setCodigoUF(35); // São Paulo
dados.setCodigoNumerico(12345678); // Código aleatório
dados.setSerie(1);
dados.setNumero(1);
dados.setDataEmissao(LocalDateTime.now());
dados.setTipoAmbiente(2); // 2=Homologação, 1=Produção
dados.setVersaoAplicativo("Meu Sistema 1.0");

// Remetente (geralmente Correios)
dados.setRemetenteCNPJ("34028316000103");
dados.setRemetenteNome("Empresa Correios LTDA");
dados.setRemetenteLogradouro("Rua Exemplo");
dados.setRemetenteNumero("100");
dados.setRemetenteBairro("Centro");
dados.setRemetenteCodigoMunicipio("3550308");
dados.setRemetenteMunicipio("São Paulo");
dados.setRemetenteUF("SP");
dados.setRemetenteCEP("01000000");

// Destinatário (obrigatório)
dados.setDestinatarioCPF("12345678901");
dados.setDestinatarioNome("João Silva");
dados.setDestinatarioLogradouro("Rua Destino");
dados.setDestinatarioNumero("200");
dados.setDestinatarioBairro("Jardim");
dados.setDestinatarioCodigoMunicipio("3550308");
dados.setDestinatarioMunicipio("São Paulo");
dados.setDestinatarioUF("SP");
dados.setDestinatarioCEP("02000000");

// Itens
List<ItemDCe> itens = new ArrayList<>();
ItemDCe item = new ItemDCe();
item.setCodigoProduto("PROD001");
item.setDescricao("Livro Técnico");
item.setNcm("49019900");
item.setQuantidade(new BigDecimal("1.0000"));
item.setValorUnitario(new BigDecimal("50.00"));
item.setValorTotal(new BigDecimal("50.00"));
item.setPeso(new BigDecimal("0.5"));
itens.add(item);

dados.setItens(itens);
dados.setValorTotal(new BigDecimal("50.00"));
dados.setCodigoRastreio("AA123456789BR");
dados.setModalidadePostagem("SEDEX");
```

### 2. Construir XML

```java
// Gera XML não assinado
String xmlNaoAssinado = DceXmlBuilder.construirXmlDCe(dados);
System.out.println(xmlNaoAssinado);
```

**Output** (exemplo):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<enviDCe xmlns="http://www.portalfiscal.inf.br/dce" versao="1.00">
  <idLote>1</idLote>
  <DCe>
    <infDCe versao="1.00" Id="DCe35251234028316000103590010000000011234567890">
      <ide>
        <cUF>35</cUF>
        <cDC>12345678</cDC>
        <mod>59</mod>
        <serie>1</serie>
        <nDC>1</nDC>
        <dhEmi>2025-12-13T10:00:00</dhEmi>
        <tpEmis>1</tpEmis>
        <cDV>0</cDV>
        <tpAmb>2</tpAmb>
        <finDCe>1</finDCe>
        <procEmi>0</procEmi>
        <verProc>Meu Sistema 1.0</verProc>
      </ide>
      <rem>...</rem>
      <dest>...</dest>
      <det nItem="1">...</det>
      <total><vDC>50.00</vDC></total>
      <transp><modFrete>9</modFrete></transp>
      <infAdic><infCpl>Código de Rastreio: AA123456789BR</infCpl></infAdic>
    </infDCe>
  </DCe>
</enviDCe>
```

### 3. Assinar Digitalmente

**Você deve implementar a assinatura** usando certificado A1 e XMLDSig:

```java
// Exemplo (você deve adaptar para sua biblioteca de assinatura)
String xmlAssinado = MinhaClasseAssinatura.assinar(xmlNaoAssinado);
```

Veja exemplo de assinatura em: `nfe/AssinaturaDigital.java`

### 4. Enviar para SEFAZ

```java
// Obter URL do webservice
String uf = "RJ"; // Estado que suporta DC-e
DceEndpoints.Ambiente ambiente = DceEndpoints.Ambiente.HOMOLOGACAO;
String url = DceEndpoints.getUrlAutorizacao(uf, ambiente);

// Enviar via SOAP (você deve implementar o cliente SOAP)
String resposta = MeuClienteSoap.enviar(url, xmlAssinado);
```

**URLs SEFAZ** (automático via `DceEndpoints`):
- **Homologação**: `https://hom.dce.sefaz.{UF}.gov.br/dce/services/DCeRecepcao`
- **Produção**: `https://dce.sefaz.{UF}.gov.br/dce/services/DCeRecepcao`

### 5. Consultar Recibo

Se SEFAZ retornar `cStat=103` (Lote recebido), aguarde 2-3 segundos e consulte:

```java
String urlConsulta = DceEndpoints.getUrlConsultaRecibo(uf, ambiente);
// Enviar consulta com número do recibo...
```

---

## 🔐 Especificações Técnicas

### Chave de Acesso (44 dígitos)

Formato: `UF(2) + AAMM(4) + CNPJ(14) + Mod(2) + Série(3) + Número(9) + TpEmis(1) + Código(8) + DV(1)`

Exemplo: `35251234028316000103590010000000011234567890`

**Algoritmo DV**: Módulo 11 (implementado em `DceXmlBuilder`)

### SOAP 1.2 Envelope

```xml
<?xml version="1.0" encoding="UTF-8"?>
<soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
  <soap12:Body>
    <dceDadosMsg xmlns="http://www.portalfiscal.inf.br/dce/wsdl/DCeRecepcao">
      <!-- XML assinado da DC-e aqui -->
    </dceDadosMsg>
  </soap12:Body>
</soap12:Envelope>
```

### Certificado Digital

- **Tipo**: A1 (PKCS#12 / .pfx)
- **Padrão**: ICP-Brasil
- **Uso**: Assinatura XMLDSig + mTLS HTTPS

---

## 📊 Códigos de Status (cStat)

| cStat | Descrição | Ação |
|-------|-----------|------|
| 100 | Autorizada | ✅ Sucesso - DC-e válida |
| 103 | Lote recebido | ⏳ Aguardar e consultar recibo |
| 104 | Lote processado | ✅ Verificar autorização de cada DC-e |
| 225 | Falha Schema XML | ❌ Corrigir estrutura do XML |
| 539 | CNPJ não credenciado | ❌ Certificado não autorizado na UF |

---

## 🧪 Testes

### Verificar UF Suportada

```java
boolean suporta = DceEndpoints.ufSuportaDCe("RJ");
System.out.println("RJ suporta DC-e: " + suporta); // true

boolean naoSuporta = DceEndpoints.ufSuportaDCe("SP");
System.out.println("SP suporta DC-e: " + naoSuporta); // false
```

### Validar Chave de Acesso

```java
String chave = "35251234028316000103590010000000011234567890";
System.out.println("Chave tem 44 dígitos: " + (chave.length() == 44));
// Último dígito é o DV calculado por Módulo 11
```

---

## 🆚 DC-e vs NFCe

| Característica | NFCe (Modelo 65) | DC-e (Modelo 59) |
|----------------|------------------|------------------|
| **Uso** | Venda ao consumidor | Declaração postal |
| **Destinatário** | Opcional | **Obrigatório** |
| **QR Code** | Obrigatório | Não possui |
| **CSC** | Necessário | Não usa |
| **Estados** | Todos os 27 | Apenas 14 |
| **Remetente** | Vendedor | Geralmente Correios |

---

## 📚 Referências

- [Portal Nacional NF-e](http://www.nfe.fazenda.gov.br/)
- [Manual de Integração DC-e v1.00](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=tW+YMyk/50s=)
- [Schemas XSD DC-e](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=/fwLvLUSmU8=)
- [Especificação Técnica ICP-Brasil](https://www.gov.br/iti/pt-br/assuntos/repositorio)

---

## ✅ Checklist de Integração

- [x] DceEndpoints.java - URLs SEFAZ
- [x] DceXmlBuilder.java - Construtor XML
- [x] DadosDCe.java - Modelo de dados
- [x] ItemDCe.java - Item da DC-e
- [ ] Implementar cliente SOAP
- [ ] Implementar assinatura XMLDSig
- [ ] Configurar certificado A1
- [ ] Testar em homologação
- [ ] Credenciar CNPJ na SEFAZ
- [ ] Deploy em produção

---

## 📝 Notas Importantes

1. **Certificado**: Você deve obter um certificado digital A1 ICP-Brasil
2. **Credenciamento**: O CNPJ deve estar credenciado na SEFAZ da UF
3. **Homologação**: Sempre teste em ambiente de homologação primeiro
4. **Estados**: Verifique se a UF suporta DC-e antes de tentar emitir
5. **SOAP**: Esta biblioteca não inclui cliente SOAP - você deve implementar

---

**Implementado em**: Dezembro 2025  
**Versão**: 1.0  
**Licença**: MIT  
**Código Sanitizado**: Sem dependências de frameworks específicos
