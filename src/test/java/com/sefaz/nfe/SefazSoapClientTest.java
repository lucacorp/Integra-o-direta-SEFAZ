package com.sefaz.nfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SefazSoapClientTest {

    @Mock
    private CertificadoDigital certificadoDigital;

    private SefazSoapClient sefazClient;

    @BeforeEach
    void setUp() {
        sefazClient = new SefazSoapClient(certificadoDigital);
    }

    @Test
    void deveExtrairCodigoStatusCorretamente() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "<cStat>100</cStat>" +
                "<xMotivo>Autorizado o uso da NF-e</xMotivo>" +
                "</retEnviNFe>";

        String codigo = sefazClient.extrairCodigoStatus(xmlResposta);

        assertEquals("100", codigo);
    }

    @Test
    void deveExtrairMensagemCorretamente() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "<cStat>100</cStat>" +
                "<xMotivo>Autorizado o uso da NF-e</xMotivo>" +
                "</retEnviNFe>";

        String mensagem = sefazClient.extrairMensagem(xmlResposta);

        assertEquals("Autorizado o uso da NF-e", mensagem);
    }

    @Test
    void deveExtrairNumeroReciboCorretamente() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "<cStat>103</cStat>" +
                "<xMotivo>Lote recebido com sucesso</xMotivo>" +
                "<infRec>" +
                "<nRec>351000123456789</nRec>" +
                "<dhRecbto>2025-12-10T10:30:00-03:00</dhRecbto>" +
                "</infRec>" +
                "</retEnviNFe>";

        String recibo = sefazClient.extrairNumeroRecibo(xmlResposta);

        assertEquals("351000123456789", recibo);
    }

    @Test
    void deveRetornarNullQuandoCodigoStatusNaoExistir() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "</retEnviNFe>";

        String codigo = sefazClient.extrairCodigoStatus(xmlResposta);

        assertNull(codigo);
    }

    @Test
    void deveRetornarNullQuandoMensagemNaoExistir() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "<cStat>100</cStat>" +
                "</retEnviNFe>";

        String mensagem = sefazClient.extrairMensagem(xmlResposta);

        assertNull(mensagem);
    }

    @Test
    void deveRetornarNullQuandoReciboNaoExistir() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "<cStat>100</cStat>" +
                "</retEnviNFe>";

        String recibo = sefazClient.extrairNumeroRecibo(xmlResposta);

        assertNull(recibo);
    }

    @Test
    void deveLancarExcecaoQuandoCertificadoNaoCarregado() {
        when(certificadoDigital.isCarregado()).thenReturn(false);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            sefazClient.enviarNFe("<NFe></NFe>", "https://exemplo.com.br");
        });

        assertEquals("Certificado digital não foi carregado.", exception.getMessage());
    }

    @Test
    void deveExtrairCodigoRejeicao() throws Exception {
        String xmlResposta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<retEnviNFe>" +
                "<tpAmb>2</tpAmb>" +
                "<cStat>215</cStat>" +
                "<xMotivo>Rejeição: Falha no schema XML da NFe</xMotivo>" +
                "</retEnviNFe>";

        String codigo = sefazClient.extrairCodigoStatus(xmlResposta);
        String mensagem = sefazClient.extrairMensagem(xmlResposta);

        assertEquals("215", codigo);
        assertEquals("Rejeição: Falha no schema XML da NFe", mensagem);
    }
}
