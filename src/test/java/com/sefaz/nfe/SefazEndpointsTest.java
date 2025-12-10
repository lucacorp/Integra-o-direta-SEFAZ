package com.sefaz.nfe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SefazEndpointsTest {

    @Test
    void deveRetornarUrlAutorizacaoSPHomologacao() {
        String url = SefazEndpoints.getUrlAutorizacao("SP", true);
        
        assertNotNull(url);
        assertTrue(url.contains("homologacao"));
        assertTrue(url.contains("nfce"));
        assertTrue(url.contains("sp.gov.br"));
    }

    @Test
    void deveRetornarUrlAutorizacaoSPProducao() {
        String url = SefazEndpoints.getUrlAutorizacao("SP", false);
        
        assertNotNull(url);
        assertFalse(url.contains("homologacao"));
        assertTrue(url.contains("nfce"));
        assertTrue(url.contains("sp.gov.br"));
    }

    @Test
    void deveRetornarUrlAutorizacaoMGHomologacao() {
        String url = SefazEndpoints.getUrlAutorizacao("MG", true);
        
        assertNotNull(url);
        assertTrue(url.contains("hnfce") || url.contains("homologacao"));
        assertTrue(url.contains("mg.gov.br"));
    }

    @Test
    void deveRetornarUrlAutorizacaoRJHomologacao() {
        String url = SefazEndpoints.getUrlAutorizacao("RJ", true);
        
        assertNotNull(url);
        assertTrue(url.contains("homologacao"));
        assertTrue(url.contains("rj.gov.br"));
    }

    @Test
    void deveRetornarUrlSVRSParaEstadoNaoMapeado() {
        String url = SefazEndpoints.getUrlAutorizacao("RS", true);
        
        assertNotNull(url);
        assertTrue(url.contains("svrs.rs.gov.br"));
    }

    @Test
    void deveRetornarUrlConsultaProtocoloSP() {
        String url = SefazEndpoints.getUrlConsultaProtocolo("SP", true);
        
        assertNotNull(url);
        assertTrue(url.contains("homologacao"));
        assertTrue(url.contains("RetAutorizacao"));
    }

    @Test
    void deveRetornarUrlStatusServicoSP() {
        String url = SefazEndpoints.getUrlStatusServico("SP", true);
        
        assertNotNull(url);
        assertTrue(url.contains("homologacao"));
        assertTrue(url.contains("StatusServico"));
    }

    @Test
    void deveTratarUFCaseInsensitive() {
        String urlMaiuscula = SefazEndpoints.getUrlAutorizacao("SP", true);
        String urlMinuscula = SefazEndpoints.getUrlAutorizacao("sp", true);
        
        assertEquals(urlMaiuscula, urlMinuscula);
    }
}
