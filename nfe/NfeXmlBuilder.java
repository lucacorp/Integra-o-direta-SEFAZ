package com.exemplo.controlemesas.nfe;

import com.exemplo.controlemesas.dto.ConfiguracaoEnderecoDTO;
import com.exemplo.controlemesas.model.ComandaResumo;
import com.exemplo.controlemesas.model.ItemComandaResumo;
import com.exemplo.controlemesas.services.ConfiguracaoService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.List;

public class NfeXmlBuilder {

    private static final DateTimeFormatter FORMATO_DATA_EMISSAO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final Random RANDOM = new Random();

    // ✅ Método antigo restaurado (compatível com NfeService)
    public static String buildNFe(ComandaResumo resumo, ConfiguracaoService cfg) {
        ZonedDateTime data = resumo.getDataFechamento().atZone(ZoneId.systemDefault());
        String dataEmissao = FORMATO_DATA_EMISSAO.format(data);

        // 1. DADOS DO EMITENTE
        String cnpj = cfg.get("empresa.cnpj", "00000000000000").replaceAll("\\D", "");
        String razao = cfg.get("empresa.razaoSocial", "Empresa Exemplo");
        String fantasia = cfg.get("empresa.nomeFantasia", "Fantasia");
        String ie = cfg.get("empresa.ie", "ISENTO").replaceAll("\\D", "");
        ConfiguracaoEnderecoDTO end = cfg.getEnderecoEmpresa();

        // 2. DADOS DA NOTA
        String cUF = "35";
        String aamm = String.format("%ty%<tm", resumo.getDataFechamento());
        String mod = "65";
        String serie = "1";
        String nNF = String.format("%09d", resumo.getId());
        String tpEmis = "1";
        String cNF = String.format("%08d", RANDOM.nextInt(100_000_000));

        // 3. CÁLCULO DA CHAVE DE ACESSO
        String baseChave = cUF + aamm + cnpj + mod + String.format("%03d", Integer.parseInt(serie)) + nNF + tpEmis + cNF;
        String cDV = String.valueOf(calcularDigitoVerificador(baseChave));
        String chave = baseChave + cDV;
        String id = "NFe" + chave;

        // 4. CONSTRUÇÃO DO XML
        StringBuilder sb = new StringBuilder();
        sb.append("<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");
        sb.append("<infNFe Id=\"").append(id).append("\" versao=\"4.00\">");

        // ide - Identificação da Nota
        sb.append("<ide>");
        sb.append("<cUF>").append(cUF).append("</cUF>");
        sb.append("<cNF>").append(cNF).append("</cNF>");
        sb.append("<natOp>VENDA</natOp>");
        sb.append("<mod>").append(mod).append("</mod>");
        sb.append("<serie>").append(serie).append("</serie>");
        sb.append("<nNF>").append(Long.parseLong(nNF)).append("</nNF>");
        sb.append("<dhEmi>").append(dataEmissao).append("</dhEmi>");
        sb.append("<tpNF>1</tpNF><idDest>1</idDest><cMunFG>").append(getCodigoMunicipio(end.getCidade())).append("</cMunFG>");
        sb.append("<tpImp>4</tpImp><tpEmis>").append(tpEmis).append("</tpEmis><cDV>").append(cDV).append("</cDV><tpAmb>2</tpAmb>");
        sb.append("<finNFe>1</finNFe><indFinal>1</indFinal><indPres>1</indPres>");
        sb.append("<procEmi>0</procEmi><verProc>1.0</verProc>");
        sb.append("</ide>");

        // emit - Emitente
        sb.append("<emit>");
        sb.append("<CNPJ>").append(cnpj).append("</CNPJ><xNome>").append(razao).append("</xNome><xFant>").append(fantasia).append("</xFant>");
        sb.append("<enderEmit>");
        sb.append("<xLgr>").append(end.getLogradouro()).append("</xLgr>");
        sb.append("<nro>").append(end.getNumero()).append("</nro>");
        sb.append("<xBairro>").append(end.getBairro()).append("</xBairro>");
        sb.append("<cMun>").append(getCodigoMunicipio(end.getCidade())).append("</cMun>");
        sb.append("<xMun>").append(end.getCidade() != null ? end.getCidade().toUpperCase() : "SAO JOSE DO RIO PARDO").append("</xMun>");
        sb.append("<UF>").append(end.getUf().toUpperCase()).append("</UF>");
        sb.append("<CEP>").append(end.getCep().replaceAll("\\D", "")).append("</CEP>");
        sb.append("<cPais>1058</cPais><xPais>BRASIL</xPais>");
        sb.append("</enderEmit>");
        sb.append("<IE>").append(ie).append("</IE><CRT>1</CRT>");
        sb.append("</emit>");

        // dest - Destinatário (usa consumidor final padrão se não houver)
        String nomeDest = resumo.getNomeCliente();
        if (nomeDest == null || nomeDest.isBlank()) {
            nomeDest = "CONSUMIDOR FINAL";
        }
        // indIEDest=9 -> Não contribuinte (consumidor final)
        sb.append("<dest><indIEDest>9</indIEDest><xNome>").append(nomeDest).append("</xNome></dest>");

        // det - Detalhes dos Produtos
        int item = 1;
        BigDecimal totalProdutos = BigDecimal.ZERO;
        List<ItemComandaResumo> itens = resumo.getItens() == null ? java.util.Collections.emptyList() : resumo.getItens();
        for (ItemComandaResumo i : itens) {
            String ncm = safeNcm(i.getNcm());
            String cfop = safe(i.getCfop(), "5102");
            String origem = safe(i.getOrigem(), "0");
            String cst = safe(i.getCst(), "102");
            boolean csosnFormat = cst != null && cst.length() == 3;

            sb.append("<det nItem=\"").append(item++).append("\">");
            sb.append("<prod><cProd>").append(i.getItemNo()).append("</cProd><xProd>").append(i.getDescricao()).append("</xProd>");
            sb.append("<NCM>").append(ncm).append("</NCM><CFOP>").append(cfop).append("</CFOP>");
            sb.append("<uCom>").append(i.getUnMedida()).append("</uCom><qCom>").append(fmt(i.getQuantidade())).append("</qCom>");
            sb.append("<vUnCom>").append(fmt(i.getPrecoUnitario())).append("</vUnCom><vProd>").append(fmt(i.getSubtotal())).append("</vProd><indTot>1</indTot></prod>");

            sb.append("<imposto>");
            if (csosnFormat) {
                sb.append("<ICMS><ICMSSN102><orig>").append(origem).append("</orig><CSOSN>").append(cst).append("</CSOSN></ICMSSN102></ICMS>");
            } else {
                sb.append("<ICMS><ICMS00><orig>").append(origem).append("</orig><CST>").append(cst).append("</CST>");
                sb.append("<modBC>0</modBC><vBC>").append(fmt(i.getSubtotal())).append("</vBC>");
                sb.append("<pICMS>").append(fmt(i.getAliqIcms())).append("</pICMS><vICMS>").append(fmt(i.getValorIcms())).append("</vICMS></ICMS00></ICMS>");
            }

            // PIS/COFINS - usa CST 49 (Outras Operações) para Simples Nacional com alíquota zero
            sb.append("<PIS><PISOutr><CST>49</CST><vBC>0.00</vBC><pPIS>0.00</pPIS><vPIS>0.00</vPIS></PISOutr></PIS>");
            sb.append("<COFINS><COFINSOutr><CST>49</CST><vBC>0.00</vBC><pCOFINS>0.00</pCOFINS><vCOFINS>0.00</vCOFINS></COFINSOutr></COFINS>");
            sb.append("</imposto></det>");

            BigDecimal subtotal = i.getSubtotal() == null ? BigDecimal.ZERO : i.getSubtotal();
            totalProdutos = totalProdutos.add(subtotal);
        }

        // total - Totais da Nota (NFe 4.0 - ordem EXATA conforme schema)
        sb.append("<total><ICMSTot>");
        sb.append("<vBC>0.00</vBC>");           // Base de cálculo ICMS
        sb.append("<vICMS>0.00</vICMS>");       // Valor total do ICMS
        sb.append("<vICMSDeson>0.00</vICMSDeson>"); // Valor ICMS desonerado
        sb.append("<vFCP>0.00</vFCP>");         // FCP (Fundo de Combate à Pobreza)
        sb.append("<vBCST>0.00</vBCST>");       // Base de cálculo ICMS ST
        sb.append("<vST>0.00</vST>");           // Valor total do ICMS ST
        sb.append("<vFCPST>0.00</vFCPST>");     // FCP retido ST
        sb.append("<vFCPSTRet>0.00</vFCPSTRet>"); // FCP retido anteriormente por ST
        sb.append("<vProd>").append(totalProdutos).append("</vProd>"); // Valor total dos produtos
        sb.append("<vFrete>0.00</vFrete>");     // Valor total do frete
        sb.append("<vSeg>0.00</vSeg>");         // Valor total do seguro
        sb.append("<vDesc>0.00</vDesc>");       // Valor total de desconto
        sb.append("<vII>0.00</vII>");           // Imposto de Importação
        sb.append("<vIPI>0.00</vIPI>");         // Valor total do IPI
        sb.append("<vIPIDevol>0.00</vIPIDevol>"); // IPI devolvido
        sb.append("<vPIS>0.00</vPIS>");         // Valor total do PIS
        sb.append("<vCOFINS>0.00</vCOFINS>");   // Valor total do COFINS
        sb.append("<vOutro>0.00</vOutro>");     // Outras despesas acessórias
        sb.append("<vNF>").append(resumo.getTotal()).append("</vNF>"); // Valor total da NF-e
        sb.append("<vTotTrib>0.00</vTotTrib>"); // Total aproximado dos tributos (Lei 12.741/12)
        sb.append("</ICMSTot></total>");

        // transp - Informações de Transporte (obrigatório NFCe)
        sb.append("<transp><modFrete>9</modFrete></transp>"); // 9=Sem frete

        // pag - Pagamento
        // Ordem correta schema NFCe: detPag > indPag, tPag, vPag, vTroco (dentro de detPag!)
        sb.append("<pag><detPag>");
        sb.append("<indPag>0</indPag>"); // Pagamento à vista
        sb.append("<tPag>01</tPag>"); // Dinheiro
        sb.append("<vPag>").append(resumo.getTotal()).append("</vPag>");
        sb.append("<vTroco>0.00</vTroco>"); // Troco obrigatório quando tPag=01
        sb.append("</detPag></pag>");

        // infAdic - Informações Adicionais (com timestamp para evitar cache da SEFAZ)
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        sb.append("<infAdic><infCpl>Obrigado pela preferencia! Pedido ").append(timestamp).append("</infCpl></infAdic>");
        sb.append("</infNFe>");
        
        // ===== QR CODE (OBRIGATÓRIO PARA NFCe) =====
        String qrCode = gerarQRCode(chave, cfg);
        sb.append("<infNFeSupl>");
        sb.append("<qrCode><![CDATA[").append(qrCode).append("]]></qrCode>");
        sb.append("</infNFeSupl>");
        
        sb.append("</NFe>");

        return sb.toString();
    }
    
    /**
     * Gera o QR Code obrigatório da NFCe.
     * URL: chNFe|tpAmb|dhEmi|vNF|digVal|idToken|CSC
     * 
     * @param chave Chave de acesso da NFCe (44 dígitos)
     * @param cfg Serviço de configuração para obter CSC
     * @return URL do QR Code
     */
    private static String gerarQRCode(String chave, ConfiguracaoService cfg) {
        try {
            // Configurações
            String tpAmb = "2"; // Homologação
            String cDest = ""; // Vazio para NFCe sem destinatário
            String dhEmi = java.time.ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                    .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            
            // Obtém CSC e ID Token do banco
            String idCSC = cfg.get("nfce.csc.id", "000001"); // ID do CSC (fornecido pela SEFAZ)
            String csc = cfg.get("nfce.csc.codigo", ""); // Código CSC (36 caracteres)
            
            if (csc.isEmpty()) {
                throw new IllegalStateException("CSC não configurado! Configure nfce.csc.codigo no banco.");
            }
            
            // Hash SHA-1: chNFe + "|" + CSC
            String textoHash = chave + "|" + idCSC + "|" + tpAmb + "|" + csc;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(textoHash.getBytes("UTF-8"));
            
            // Converte hash para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String cHashQRCode = hexString.toString().toUpperCase();
            
            // Monta URL do QR Code (SP Homologação)
            String urlQRCode = "https://www.homologacao.nfce.fazenda.sp.gov.br/NFCeConsultaPublica/Paginas/ConsultaQRCode.aspx";
            
            return urlQRCode + "?p=" + chave + "|" + tpAmb + "|" + idCSC + "|" + cHashQRCode;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code da NFCe: " + e.getMessage(), e);
        }
    }

    // 🔢 Cálculo do dígito verificador
    private static int calcularDigitoVerificador(String chave43) {
        int soma = 0, peso = 2;
        for (int i = chave43.length() - 1; i >= 0; i--) {
            int num = Character.getNumericValue(chave43.charAt(i));
            soma += num * peso;
            peso = (peso == 9) ? 2 : peso + 1;
        }
        int mod = soma % 11;
        return (mod == 0 || mod == 1) ? 0 : 11 - mod;
    }

    // 📍 Código IBGE das principais cidades
    private static String getCodigoMunicipio(String nomeCidade) {
        if (nomeCidade == null) return "3549706";
        String cidade = nomeCidade.trim().toUpperCase(Locale.ROOT);
        return switch (cidade) {
            case "SÃO PAULO" -> "3550308";
            case "SÃO JOSÉ DO RIO PRETO" -> "3549805";
            case "SÃO JOSÉ DO RIO PARDO" -> "3549706";
            case "RIBEIRÃO PRETO" -> "3543402";
            default -> "3549706";
        };
    }

    // ⚙️ Funções utilitárias
    private static String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static String safeNcm(String ncm) {
        if (ncm == null || ncm.isBlank()) return "19059090";
        String onlyDigits = ncm.replaceAll("\\D", "");
        if (onlyDigits.length() == 8) return onlyDigits;
        if (onlyDigits.length() > 8) return onlyDigits.substring(0, 8);
        return String.format("%1$8s", onlyDigits).replace(' ', '0');
    }

    private static String fmt(BigDecimal value) {
        if (value == null) return "0.00";
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
