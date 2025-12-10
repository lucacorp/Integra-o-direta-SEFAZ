package com.sefaz.nfe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Realiza a assinatura digital XML conforme padrão NFe.
 * 
 * Implementa a assinatura XML usando:
 * - Algoritmo de assinatura: RSA-SHA1
 * - Algoritmo de digest: SHA1
 * - Canonicalização: Inclusive
 * - Transform: Enveloped
 * 
 * @author Comunidade Open Source
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssinaturaDigital {

    private final CertificadoDigital certificadoDigital;

    /**
     * Assina o XML da NF-e.
     *
     * @param xml XML sem assinatura
     * @return XML assinado
     * @throws Exception Se houver erro na assinatura
     */
    public String assinar(String xml) throws Exception {
        if (!certificadoDigital.isCarregado()) {
            throw new IllegalStateException("Certificado digital não foi carregado.");
        }

        log.debug("Iniciando assinatura do XML");

        // Parse do XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        // Localiza o elemento infNFe (que contém o atributo Id)
        NodeList infNFeList = doc.getElementsByTagNameNS("http://www.portalfiscal.inf.br/nfe", "infNFe");
        if (infNFeList.getLength() == 0) {
            throw new IllegalArgumentException("Elemento infNFe não encontrado no XML");
        }

        Element infNFe = (Element) infNFeList.item(0);
        String id = infNFe.getAttribute("Id");
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Atributo Id não encontrado em infNFe");
        }

        log.debug("Assinando elemento com Id: {}", id);

        // Configuração da assinatura
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Reference ao elemento a ser assinado
        Reference ref = fac.newReference(
                "#" + id,
                fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(
                        fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)
                ),
                null,
                null
        );

        // SignedInfo
        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(
                        CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null
                ),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref)
        );

        // KeyInfo (informações do certificado)
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List<Object> x509Content = new ArrayList<>();
        x509Content.add(certificadoDigital.getCertificate());
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        // Cria a assinatura
        PrivateKey privateKey = certificadoDigital.getPrivateKey();
        DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

        XMLSignature signature = fac.newXMLSignature(si, ki);
        signature.sign(dsc);

        log.debug("Assinatura digital realizada com sucesso");

        // Converte de volta para String
        return documentToString(doc);
    }

    /**
     * Converte um Document XML para String.
     */
    private String documentToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
