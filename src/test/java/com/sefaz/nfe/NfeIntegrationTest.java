package com.sefaz.nfe;

import com.sefaz.model.ComandaResumo;
import com.sefaz.model.ItemComandaResumo;
import com.sefaz.services.ConfiguracaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes de integração para validação do fluxo completo de NF-e.
 * Estes testes validam a geração e estrutura do XML, mas não fazem chamadas reais à SEFAZ.
 */
@ExtendWith(MockitoExtension.class)
class NfeIntegrationTest {

    @Mock
    private ConfiguracaoService cfg;

    @BeforeEach
    void setUp() {
        // Configura valores padrão
        when(cfg.get("empresa.cnpj", "00000000000000")).thenReturn("12345678000190");
        when(cfg.get("empresa.razaoSocial", "Empresa Exemplo")).thenReturn("EMPRESA TESTE LTDA");
        when(cfg.get("empresa.nomeFantasia", "Fantasia")).thenReturn("EMPRESA TESTE");
        when(cfg.get("empresa.ie", "ISENTO")).thenReturn("123456789");
        when(cfg.get("empresa.uf", "SP")).thenReturn("SP");
    }

    @Test
    void deveGerarXmlNFeValido() throws Exception {
        ComandaResumo resumo = criarResumoTeste();

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertNotNull(xml);
        assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(xml.contains("<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">"));
        assertTrue(xml.contains("<infNFe versao=\"4.00\""));
        assertTrue(xml.contains("Id=\"NFe"));
    }

    @Test
    void deveGerarChaveAcessoComTamanhoCorreto() throws Exception {
        ComandaResumo resumo = criarResumoTeste();

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        // Extrai a chave de acesso
        String chave = extrairChaveDoXml(xml);

        assertNotNull(chave);
        assertEquals(44, chave.length(), "Chave de acesso deve ter 44 dígitos");
        assertTrue(chave.matches("\\d{44}"), "Chave de acesso deve conter apenas números");
    }

    @Test
    void deveIncluirDadosEmitente() throws Exception {
        ComandaResumo resumo = criarResumoTeste();

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertTrue(xml.contains("<emit>"));
        assertTrue(xml.contains("<CNPJ>12345678000190</CNPJ>"));
        assertTrue(xml.contains("<xNome>EMPRESA TESTE LTDA</xNome>"));
        assertTrue(xml.contains("<xFant>EMPRESA TESTE</xFant>"));
        assertTrue(xml.contains("<IE>123456789</IE>"));
    }

    @Test
    void deveIncluirTodosItens() throws Exception {
        ComandaResumo resumo = criarResumoTeste();

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        // Verifica que todos os 3 itens estão no XML
        assertTrue(xml.contains("nItem=\"1\""));
        assertTrue(xml.contains("nItem=\"2\""));
        assertTrue(xml.contains("nItem=\"3\""));
        assertTrue(xml.contains("<xProd>COCA-COLA 350ML</xProd>"));
        assertTrue(xml.contains("<xProd>X-BURGER</xProd>"));
        assertTrue(xml.contains("<xProd>BATATA FRITA</xProd>"));
    }

    @Test
    void deveCalcularTotalCorretamente() throws Exception {
        ComandaResumo resumo = criarResumoTeste();
        resumo.setTotal(new BigDecimal("45.00")); // 2*5 + 1*25 + 1*10 = 45

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertTrue(xml.contains("<vNF>45.00</vNF>") || xml.contains("<vNF>45.0</vNF>"));
    }

    @Test
    void deveIncluirDestinatarioConsumidorFinal() throws Exception {
        ComandaResumo resumo = criarResumoTeste();
        resumo.setNomeCliente(null); // Sem nome de cliente

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertTrue(xml.contains("<dest>"));
        assertTrue(xml.contains("CONSUMIDOR FINAL"));
    }

    @Test
    void deveIncluirDestinatarioQuandoInformado() throws Exception {
        ComandaResumo resumo = criarResumoTeste();
        resumo.setNomeCliente("JOÃO DA SILVA");

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertTrue(xml.contains("<dest>"));
        assertTrue(xml.contains("JOÃO DA SILVA"));
    }

    @Test
    void deveValidarEstruturaDosImpostos() throws Exception {
        ComandaResumo resumo = criarResumoTeste();

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertTrue(xml.contains("<imposto>"));
        assertTrue(xml.contains("<ICMS>"));
        assertTrue(xml.contains("<PIS>"));
        assertTrue(xml.contains("<COFINS>"));
    }

    @Test
    void deveIncluirInformacoesIdentificacao() throws Exception {
        ComandaResumo resumo = criarResumoTeste();

        String xml = NfeXmlBuilder.buildNFe(resumo, cfg);

        assertTrue(xml.contains("<ide>"));
        assertTrue(xml.contains("<mod>65</mod>")); // Modelo 65 = NFC-e
        assertTrue(xml.contains("<tpNF>1</tpNF>")); // Tipo 1 = Saída
        assertTrue(xml.contains("<indFinal>1</indFinal>")); // Consumidor final
        assertTrue(xml.contains("<indPres>1</indPres>")); // Operação presencial
    }

    // Métodos auxiliares

    private ComandaResumo criarResumoTeste() {
        ComandaResumo resumo = new ComandaResumo();
        resumo.setId(1L);
        resumo.setNomeCliente("Cliente Teste");
        resumo.setTotal(new BigDecimal("45.00"));
        resumo.setDataFechamento(LocalDateTime.now());
        
        List<ItemComandaResumo> itens = new ArrayList<>();
        
        // Item 1: Bebida
        ItemComandaResumo item1 = new ItemComandaResumo();
        item1.setItemNo(1);
        item1.setDescricao("COCA-COLA 350ML");
        item1.setQuantidade(new BigDecimal("2"));
        item1.setPrecoUnitario(new BigDecimal("5.00"));
        item1.setSubtotal(new BigDecimal("10.00"));
        item1.setUnMedida("UN");
        item1.setNcm("22021000");
        item1.setCfop("5102");
        item1.setOrigem("0");
        item1.setCst("102");
        item1.setAliqIcms(BigDecimal.ZERO);
        item1.setValorIcms(BigDecimal.ZERO);
        item1.setAliqPis(new BigDecimal("1.65"));
        item1.setValorPis(new BigDecimal("0.17"));
        item1.setAliqCofins(new BigDecimal("7.60"));
        item1.setValorCofins(new BigDecimal("0.76"));
        itens.add(item1);
        
        // Item 2: Lanche
        ItemComandaResumo item2 = new ItemComandaResumo();
        item2.setItemNo(2);
        item2.setDescricao("X-BURGER");
        item2.setQuantidade(new BigDecimal("1"));
        item2.setPrecoUnitario(new BigDecimal("25.00"));
        item2.setSubtotal(new BigDecimal("25.00"));
        item2.setUnMedida("UN");
        item2.setNcm("19059090");
        item2.setCfop("5102");
        item2.setOrigem("0");
        item2.setCst("102");
        item2.setAliqIcms(BigDecimal.ZERO);
        item2.setValorIcms(BigDecimal.ZERO);
        item2.setAliqPis(new BigDecimal("1.65"));
        item2.setValorPis(new BigDecimal("0.41"));
        item2.setAliqCofins(new BigDecimal("7.60"));
        item2.setValorCofins(new BigDecimal("1.90"));
        itens.add(item2);
        
        // Item 3: Porção
        ItemComandaResumo item3 = new ItemComandaResumo();
        item3.setItemNo(3);
        item3.setDescricao("BATATA FRITA");
        item3.setQuantidade(new BigDecimal("1"));
        item3.setPrecoUnitario(new BigDecimal("10.00"));
        item3.setSubtotal(new BigDecimal("10.00"));
        item3.setUnMedida("UN");
        item3.setNcm("20041000");
        item3.setCfop("5102");
        item3.setOrigem("0");
        item3.setCst("102");
        item3.setAliqIcms(BigDecimal.ZERO);
        item3.setValorIcms(BigDecimal.ZERO);
        item3.setAliqPis(new BigDecimal("1.65"));
        item3.setValorPis(new BigDecimal("0.17"));
        item3.setAliqCofins(new BigDecimal("7.60"));
        item3.setValorCofins(new BigDecimal("0.76"));
        itens.add(item3);
        
        resumo.setItens(itens);
        
        return resumo;
    }

    private String extrairChaveDoXml(String xml) {
        int idxStart = xml.indexOf("Id=\"NFe") + 7;
        int idxEnd = xml.indexOf("\"", idxStart);
        return xml.substring(idxStart, idxEnd);
    }
}
