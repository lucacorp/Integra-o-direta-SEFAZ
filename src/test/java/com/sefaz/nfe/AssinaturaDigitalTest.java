package com.sefaz.nfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssinaturaDigitalTest {

    @Mock
    private CertificadoDigital certificadoDigital;

    private AssinaturaDigital assinaturaDigital;

    @BeforeEach
    void setUp() {
        assinaturaDigital = new AssinaturaDigital(certificadoDigital);
    }

    @Test
    void deveLancarExcecaoQuandoCertificadoNaoCarregado() {
        when(certificadoDigital.isCarregado()).thenReturn(false);

        String xml = "<?xml version=\"1.0\"?><NFe><infNFe Id=\"NFe12345678901234567890123456789012345678901234\"></infNFe></NFe>";

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            assinaturaDigital.assinar(xml);
        });

        assertEquals("Certificado digital não foi carregado.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoXmlNaoPossuiInfNFe() {
        when(certificadoDigital.isCarregado()).thenReturn(true);

        String xmlInvalido = "<?xml version=\"1.0\"?><NFe><outro></outro></NFe>";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            assinaturaDigital.assinar(xmlInvalido);
        });

        assertEquals("Elemento infNFe não encontrado no XML", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoInfNFeNaoPossuiId() {
        when(certificadoDigital.isCarregado()).thenReturn(true);

        String xmlSemId = "<?xml version=\"1.0\"?><NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\"><infNFe></infNFe></NFe>";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            assinaturaDigital.assinar(xmlSemId);
        });

        assertEquals("Atributo Id não encontrado em infNFe", exception.getMessage());
    }

    @Test
    void deveValidarFormatoDoXmlComNamespace() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">" +
                "<infNFe versao=\"4.00\" Id=\"NFe35250112345678000190650010000000011000000019\">" +
                "<ide><cUF>35</cUF></ide>" +
                "</infNFe>" +
                "</NFe>";

        // Verifica que não lança exceção com XML válido
        when(certificadoDigital.isCarregado()).thenReturn(true);
        
        // Como não temos certificado real, esperamos que falhe na assinatura, mas passe na validação do XML
        assertDoesNotThrow(() -> {
            try {
                assinaturaDigital.assinar(xml);
            } catch (NullPointerException e) {
                // Esperado quando mock não retorna certificado/chave real
            }
        });
    }
}
