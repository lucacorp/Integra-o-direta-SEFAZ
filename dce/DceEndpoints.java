package br.gov.sefaz.dce;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoints dos webservices DC-e (Declaração de Conteúdo Eletrônica) da SEFAZ.
 * 
 * A DC-e é utilizada pelos Correios para declaração de conteúdo de encomendas.
 * Disponível apenas em alguns estados: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO
 */
public class DceEndpoints {

    /**
     * Ambientes disponíveis
     */
    public enum Ambiente {
        PRODUCAO(1),
        HOMOLOGACAO(2);

        private final int codigo;

        Ambiente(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }
    }

    /**
     * URLs de Autorização DC-e por UF (Homologação)
     */
    private static final Map<String, String> URLS_AUTORIZACAO_HOMOLOGACAO = new HashMap<>();

    /**
     * URLs de Autorização DC-e por UF (Produção)
     */
    private static final Map<String, String> URLS_AUTORIZACAO_PRODUCAO = new HashMap<>();

    /**
     * URLs de Consulta Recibo DC-e por UF (Homologação)
     */
    private static final Map<String, String> URLS_CONSULTA_HOMOLOGACAO = new HashMap<>();

    /**
     * URLs de Consulta Recibo DC-e por UF (Produção)
     */
    private static final Map<String, String> URLS_CONSULTA_PRODUCAO = new HashMap<>();

    static {
        // HOMOLOGAÇÃO - Autorização DC-e
        URLS_AUTORIZACAO_HOMOLOGACAO.put("AC", "https://hom.dce.sefaznet.ac.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("AL", "https://hom.dce.sefaz.al.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("AP", "https://hom.dce.sefaz.ap.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("DF", "https://hom.dce.fazenda.df.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("ES", "https://hom.dce.sefaz.es.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("PB", "https://hom.dce.sefaz.pb.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("PI", "https://hom.dce.sefaz.pi.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("RJ", "https://hom.dce.fazenda.rj.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("RN", "https://hom.dce.sefaz.rn.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("RO", "https://hom.dce.sefaz.ro.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("RR", "https://hom.dce.sefaz.rr.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("SC", "https://hom.dce.sefaz.sc.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("SE", "https://hom.dce.sefaz.se.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_HOMOLOGACAO.put("TO", "https://hom.dce.sefaz.to.gov.br/dce/services/DCeRecepcao");

        // HOMOLOGAÇÃO - Consulta Recibo DC-e
        URLS_CONSULTA_HOMOLOGACAO.put("AC", "https://hom.dce.sefaznet.ac.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("AL", "https://hom.dce.sefaz.al.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("AP", "https://hom.dce.sefaz.ap.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("DF", "https://hom.dce.fazenda.df.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("ES", "https://hom.dce.sefaz.es.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("PB", "https://hom.dce.sefaz.pb.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("PI", "https://hom.dce.sefaz.pi.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("RJ", "https://hom.dce.fazenda.rj.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("RN", "https://hom.dce.sefaz.rn.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("RO", "https://hom.dce.sefaz.ro.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("RR", "https://hom.dce.sefaz.rr.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("SC", "https://hom.dce.sefaz.sc.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("SE", "https://hom.dce.sefaz.se.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_HOMOLOGACAO.put("TO", "https://hom.dce.sefaz.to.gov.br/dce/services/DCeRetRecepcao");

        // PRODUÇÃO - Autorização DC-e
        URLS_AUTORIZACAO_PRODUCAO.put("AC", "https://dce.sefaznet.ac.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("AL", "https://dce.sefaz.al.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("AP", "https://dce.sefaz.ap.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("DF", "https://dce.fazenda.df.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("ES", "https://dce.sefaz.es.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("PB", "https://dce.sefaz.pb.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("PI", "https://dce.sefaz.pi.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("RJ", "https://dce.fazenda.rj.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("RN", "https://dce.sefaz.rn.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("RO", "https://dce.sefaz.ro.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("RR", "https://dce.sefaz.rr.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("SC", "https://dce.sefaz.sc.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("SE", "https://dce.sefaz.se.gov.br/dce/services/DCeRecepcao");
        URLS_AUTORIZACAO_PRODUCAO.put("TO", "https://dce.sefaz.to.gov.br/dce/services/DCeRecepcao");

        // PRODUÇÃO - Consulta Recibo DC-e
        URLS_CONSULTA_PRODUCAO.put("AC", "https://dce.sefaznet.ac.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("AL", "https://dce.sefaz.al.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("AP", "https://dce.sefaz.ap.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("DF", "https://dce.fazenda.df.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("ES", "https://dce.sefaz.es.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("PB", "https://dce.sefaz.pb.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("PI", "https://dce.sefaz.pi.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("RJ", "https://dce.fazenda.rj.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("RN", "https://dce.sefaz.rn.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("RO", "https://dce.sefaz.ro.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("RR", "https://dce.sefaz.rr.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("SC", "https://dce.sefaz.sc.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("SE", "https://dce.sefaz.se.gov.br/dce/services/DCeRetRecepcao");
        URLS_CONSULTA_PRODUCAO.put("TO", "https://dce.sefaz.to.gov.br/dce/services/DCeRetRecepcao");
    }

    /**
     * Retorna a URL de autorização DC-e para a UF e ambiente especificados
     */
    public static String getUrlAutorizacao(String uf, Ambiente ambiente) {
        Map<String, String> urls = ambiente == Ambiente.PRODUCAO ? 
            URLS_AUTORIZACAO_PRODUCAO : URLS_AUTORIZACAO_HOMOLOGACAO;
        
        String url = urls.get(uf.toUpperCase());
        if (url == null) {
            throw new IllegalArgumentException(
                "UF " + uf + " não suporta DC-e. Estados disponíveis: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO"
            );
        }
        return url;
    }

    /**
     * Retorna a URL de consulta de recibo DC-e para a UF e ambiente especificados
     */
    public static String getUrlConsultaRecibo(String uf, Ambiente ambiente) {
        Map<String, String> urls = ambiente == Ambiente.PRODUCAO ? 
            URLS_CONSULTA_PRODUCAO : URLS_CONSULTA_HOMOLOGACAO;
        
        String url = urls.get(uf.toUpperCase());
        if (url == null) {
            throw new IllegalArgumentException(
                "UF " + uf + " não suporta DC-e. Estados disponíveis: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO"
            );
        }
        return url;
    }

    /**
     * Verifica se a UF suporta DC-e
     */
    public static boolean ufSuportaDCe(String uf) {
        return URLS_AUTORIZACAO_HOMOLOGACAO.containsKey(uf.toUpperCase());
    }
}
