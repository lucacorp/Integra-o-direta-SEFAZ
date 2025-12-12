package com.exemplo.controlemesas.nfe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Cliente SOAP para comunicação com os webservices da SEFAZ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SefazSoapClient {

    private final CertificadoDigital certificadoDigital;

    /**
     * Envia uma NFC-e para autorização na SEFAZ.
     *
     * @param xmlAssinado XML da NF-e assinado
     * @param url URL do webservice da SEFAZ
     * @return XML de resposta da SEFAZ
     */
    public String enviarNFe(String xmlAssinado, String url) throws Exception {
        log.info("Enviando NF-e para SEFAZ: {}", url);

        // Monta o envelope SOAP (sem Base64, XML direto)
        String soapEnvelope = buildSoapEnvelope(xmlAssinado);
        
        log.debug("Envelope SOAP gerado:\n{}", soapEnvelope);

        // Envia via HTTPS com certificado digital
        String resposta = enviarSoap(url, soapEnvelope);

        log.debug("Resposta SEFAZ recebida:\n{}", resposta);

        return resposta;
    }

    /**
     * Consulta o recibo de uma NF-e enviada.
     *
     * @param numeroRecibo Número do recibo retornado pela SEFAZ
     * @param url URL do webservice de consulta
     * @return XML de resposta da SEFAZ
     */
    public String consultarRecibo(String numeroRecibo, String url) throws Exception {
        log.info("Consultando recibo {} na SEFAZ: {}", numeroRecibo, url);

        String soapBody = String.format(
                "<consReciNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                        "<tpAmb>2</tpAmb>" +
                        "<nRec>%s</nRec>" +
                        "</consReciNFe>",
                numeroRecibo
        );

        String soapEnvelope = buildConsultaSoapEnvelope(soapBody);

        String resposta = enviarSoap(url, soapEnvelope);

        log.debug("Resposta de consulta recebida");

        return resposta;
    }

    /**
     * Envia requisição SOAP via HTTPS com certificado digital.
     */
    private String enviarSoap(String url, String soapEnvelope) throws Exception {
        if (!certificadoDigital.isCarregado()) {
            throw new IllegalStateException("Certificado digital não foi carregado.");
        }

        // Configura SSL com o certificado digital e trust manager permissivo para SEFAZ
        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(
                        certificadoDigital.getKeyStore(),
                        certificadoDigital.getSenha().toCharArray()
                )
                .loadTrustMaterial(null, (chain, authType) -> true) // Aceita certificados da SEFAZ
                .build();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE // Aceita qualquer hostname
        );

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(
                        PoolingHttpClientConnectionManagerBuilder.create()
                                .setSSLSocketFactory(sslSocketFactory)
                                .build()
                )
                .build()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/soap+xml; charset=utf-8");
            httpPost.setEntity(new StringEntity(soapEnvelope, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                String responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

                log.info("Status HTTP: {}", statusCode);
                log.debug("Corpo da resposta:\n{}", responseBody);

                if (statusCode != 200) {
                    log.error("Erro HTTP {} ao comunicar com SEFAZ. Resposta:\n{}", statusCode, responseBody);
                    throw new RuntimeException("Erro HTTP " + statusCode + " ao comunicar com SEFAZ");
                }

                return responseBody;
            }
        }
    }

    /**
     * Constrói o envelope SOAP para autorização de NF-e.
     */
    private String buildSoapEnvelope(String xmlNFe) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Body>" +
                "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4\">" +
                xmlNFe +
                "</nfeDadosMsg>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";
    }

    /**
     * Constrói o envelope SOAP para consulta de recibo.
     */
    private String buildConsultaSoapEnvelope(String xmlConsulta) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Body>" +
                "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4\">" +
                xmlConsulta +
                "</nfeDadosMsg>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";
    }

    /**
     * Extrai o código de status da resposta SOAP da SEFAZ.
     */
    public String extrairCodigoStatus(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes(StandardCharsets.UTF_8)));

        // Procura por cStat no XML de resposta
        NodeList cStatList = doc.getElementsByTagName("cStat");
        if (cStatList.getLength() > 0) {
            return cStatList.item(0).getTextContent();
        }

        return null;
    }

    /**
     * Extrai a mensagem de retorno da SEFAZ.
     */
    public String extrairMensagem(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes(StandardCharsets.UTF_8)));

        NodeList xMotivoList = doc.getElementsByTagName("xMotivo");
        if (xMotivoList.getLength() > 0) {
            return xMotivoList.item(0).getTextContent();
        }

        return null;
    }

    /**
     * Extrai o número do recibo da resposta de autorização.
     */
    public String extrairNumeroRecibo(String xmlResposta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlResposta.getBytes(StandardCharsets.UTF_8)));

        NodeList nRecList = doc.getElementsByTagName("nRec");
        if (nRecList.getLength() > 0) {
            return nRecList.item(0).getTextContent();
        }

        return null;
    }
}
