package com.exemplo.controlemesas.nfe;

import lombok.Data;

/**
 * URLs dos webservices da SEFAZ por UF e ambiente.
 */
@Data
public class SefazEndpoints {

    /**
     * Retorna a URL do serviço de autorização de NFC-e para a UF e ambiente especificados.
     *
     * @param uf UF (ex: SP, MG, RJ)
     * @param homologacao true para homologação, false para produção
     * @return URL do webservice
     */
    public static String getUrlAutorizacao(String uf, boolean homologacao) {
        // São Paulo
        if ("SP".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeAutorizacao4.asmx"
                    : "https://nfce.fazenda.sp.gov.br/ws/NFeAutorizacao4.asmx";
        }

        // Minas Gerais
        if ("MG".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeAutorizacao4"
                    : "https://nfce.fazenda.mg.gov.br/nfce/services/NFeAutorizacao4";
        }

        // Rio de Janeiro
        if ("RJ".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://homologacao.nfce.fazenda.rj.gov.br/NFeAutorizacao4/NFeAutorizacao4.asmx"
                    : "https://nfce.fazenda.rj.gov.br/NFeAutorizacao4/NFeAutorizacao4.asmx";
        }

        // SVRS (demais estados)
        return homologacao
                ? "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx"
                : "https://nfce.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx";
    }

    /**
     * Retorna a URL do serviço de consulta de protocolo.
     *
     * @param uf UF (ex: SP, MG, RJ)
     * @param homologacao true para homologação, false para produção
     * @return URL do webservice
     */
    public static String getUrlConsultaProtocolo(String uf, boolean homologacao) {
        if ("SP".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeRetAutorizacao4.asmx"
                    : "https://nfce.fazenda.sp.gov.br/ws/NFeRetAutorizacao4.asmx";
        }

        if ("MG".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeRetAutorizacao4"
                    : "https://nfce.fazenda.mg.gov.br/nfce/services/NFeRetAutorizacao4";
        }

        if ("RJ".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://homologacao.nfce.fazenda.rj.gov.br/NFeRetAutorizacao4/NFeRetAutorizacao4.asmx"
                    : "https://nfce.fazenda.rj.gov.br/NFeRetAutorizacao4/NFeRetAutorizacao4.asmx";
        }

        return homologacao
                ? "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"
                : "https://nfce.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx";
    }

    /**
     * Retorna a URL do serviço de consulta de status do serviço.
     *
     * @param uf UF (ex: SP, MG, RJ)
     * @param homologacao true para homologação, false para produção
     * @return URL do webservice
     */
    public static String getUrlStatusServico(String uf, boolean homologacao) {
        if ("SP".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeStatusServico4.asmx"
                    : "https://nfce.fazenda.sp.gov.br/ws/NFeStatusServico4.asmx";
        }

        if ("MG".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeStatusServico4"
                    : "https://nfce.fazenda.mg.gov.br/nfce/services/NFeStatusServico4";
        }

        if ("RJ".equalsIgnoreCase(uf)) {
            return homologacao
                    ? "https://homologacao.nfce.fazenda.rj.gov.br/NFeStatusServico4/NFeStatusServico4.asmx"
                    : "https://nfce.fazenda.rj.gov.br/NFeStatusServico4/NFeStatusServico4.asmx";
        }

        return homologacao
                ? "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeStatusServico/NFeStatusServico4.asmx"
                : "https://nfce.svrs.rs.gov.br/ws/NfeStatusServico/NFeStatusServico4.asmx";
    }
}
