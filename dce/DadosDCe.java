package br.gov.sefaz.dce;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dados necessários para construção de uma DC-e (Declaração de Conteúdo Eletrônica).
 * 
 * Este modelo contém todas as informações obrigatórias e opcionais para emissão
 * de uma DC-e (modelo 59) junto à SEFAZ.
 */
public class DadosDCe {
    
    // Identificação da DC-e
    private Integer numeroLote;
    private Integer codigoUF;
    private Integer codigoNumerico;
    private Integer modelo = 59; // Modelo 59 para DC-e
    private Integer serie;
    private Integer numero;
    private LocalDateTime dataEmissao;
    private Integer tipoEmissao = 1; // 1=Normal
    private Integer tipoAmbiente; // 1=Produção, 2=Homologação
    private Integer finalidade = 1; // 1=DC-e normal
    private Integer processoEmissao = 0; // 0=Aplicativo próprio
    private String versaoAplicativo;
    
    // Remetente (quem envia - geralmente Correios)
    private String remetenteCNPJ;
    private String remetenteNome;
    private String remetenteLogradouro;
    private String remetenteNumero;
    private String remetenteComplemento;
    private String remetenteBairro;
    private String remetenteCodigoMunicipio;
    private String remetenteMunicipio;
    private String remetenteUF;
    private String remetenteCEP;
    
    // Destinatário (quem recebe)
    private String destinatarioCNPJ;
    private String destinatarioCPF;
    private String destinatarioNome;
    private String destinatarioLogradouro;
    private String destinatarioNumero;
    private String destinatarioComplemento;
    private String destinatarioBairro;
    private String destinatarioCodigoMunicipio;
    private String destinatarioMunicipio;
    private String destinatarioUF;
    private String destinatarioCEP;
    
    // Itens/Produtos
    private List<ItemDCe> itens;
    
    // Totais
    private BigDecimal valorTotal;
    
    // Transporte
    private Integer modalidadeFrete = 9; // 9=Sem frete (padrão DC-e)
    
    // Informações adicionais (Correios)
    private String codigoRastreio;
    private String modalidadePostagem; // SEDEX, PAC, etc.
    private BigDecimal pesoTotal; // em kg
    private String objetoPostal; // Tipo de objeto (caixa, envelope, etc.)

    // Getters e Setters
    public Integer getNumeroLote() { return numeroLote; }
    public void setNumeroLote(Integer numeroLote) { this.numeroLote = numeroLote; }

    public Integer getCodigoUF() { return codigoUF; }
    public void setCodigoUF(Integer codigoUF) { this.codigoUF = codigoUF; }

    public Integer getCodigoNumerico() { return codigoNumerico; }
    public void setCodigoNumerico(Integer codigoNumerico) { this.codigoNumerico = codigoNumerico; }

    public Integer getModelo() { return modelo; }
    public void setModelo(Integer modelo) { this.modelo = modelo; }

    public Integer getSerie() { return serie; }
    public void setSerie(Integer serie) { this.serie = serie; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }

    public Integer getTipoEmissao() { return tipoEmissao; }
    public void setTipoEmissao(Integer tipoEmissao) { this.tipoEmissao = tipoEmissao; }

    public Integer getTipoAmbiente() { return tipoAmbiente; }
    public void setTipoAmbiente(Integer tipoAmbiente) { this.tipoAmbiente = tipoAmbiente; }

    public Integer getFinalidade() { return finalidade; }
    public void setFinalidade(Integer finalidade) { this.finalidade = finalidade; }

    public Integer getProcessoEmissao() { return processoEmissao; }
    public void setProcessoEmissao(Integer processoEmissao) { this.processoEmissao = processoEmissao; }

    public String getVersaoAplicativo() { return versaoAplicativo; }
    public void setVersaoAplicativo(String versaoAplicativo) { this.versaoAplicativo = versaoAplicativo; }

    public String getRemetenteCNPJ() { return remetenteCNPJ; }
    public void setRemetenteCNPJ(String remetenteCNPJ) { this.remetenteCNPJ = remetenteCNPJ; }

    public String getRemetenteNome() { return remetenteNome; }
    public void setRemetenteNome(String remetenteNome) { this.remetenteNome = remetenteNome; }

    public String getRemetenteLogradouro() { return remetenteLogradouro; }
    public void setRemetenteLogradouro(String remetenteLogradouro) { this.remetenteLogradouro = remetenteLogradouro; }

    public String getRemetenteNumero() { return remetenteNumero; }
    public void setRemetenteNumero(String remetenteNumero) { this.remetenteNumero = remetenteNumero; }

    public String getRemetenteComplemento() { return remetenteComplemento; }
    public void setRemetenteComplemento(String remetenteComplemento) { this.remetenteComplemento = remetenteComplemento; }

    public String getRemetenteBairro() { return remetenteBairro; }
    public void setRemetenteBairro(String remetenteBairro) { this.remetenteBairro = remetenteBairro; }

    public String getRemetenteCodigoMunicipio() { return remetenteCodigoMunicipio; }
    public void setRemetenteCodigoMunicipio(String remetenteCodigoMunicipio) { this.remetenteCodigoMunicipio = remetenteCodigoMunicipio; }

    public String getRemetenteMunicipio() { return remetenteMunicipio; }
    public void setRemetenteMunicipio(String remetenteMunicipio) { this.remetenteMunicipio = remetenteMunicipio; }

    public String getRemetenteUF() { return remetenteUF; }
    public void setRemetenteUF(String remetenteUF) { this.remetenteUF = remetenteUF; }

    public String getRemetenteCEP() { return remetenteCEP; }
    public void setRemetenteCEP(String remetenteCEP) { this.remetenteCEP = remetenteCEP; }

    public String getDestinatarioCNPJ() { return destinatarioCNPJ; }
    public void setDestinatarioCNPJ(String destinatarioCNPJ) { this.destinatarioCNPJ = destinatarioCNPJ; }

    public String getDestinatarioCPF() { return destinatarioCPF; }
    public void setDestinatarioCPF(String destinatarioCPF) { this.destinatarioCPF = destinatarioCPF; }

    public String getDestinatarioNome() { return destinatarioNome; }
    public void setDestinatarioNome(String destinatarioNome) { this.destinatarioNome = destinatarioNome; }

    public String getDestinatarioLogradouro() { return destinatarioLogradouro; }
    public void setDestinatarioLogradouro(String destinatarioLogradouro) { this.destinatarioLogradouro = destinatarioLogradouro; }

    public String getDestinatarioNumero() { return destinatarioNumero; }
    public void setDestinatarioNumero(String destinatarioNumero) { this.destinatarioNumero = destinatarioNumero; }

    public String getDestinatarioComplemento() { return destinatarioComplemento; }
    public void setDestinatarioComplemento(String destinatarioComplemento) { this.destinatarioComplemento = destinatarioComplemento; }

    public String getDestinatarioBairro() { return destinatarioBairro; }
    public void setDestinatarioBairro(String destinatarioBairro) { this.destinatarioBairro = destinatarioBairro; }

    public String getDestinatarioCodigoMunicipio() { return destinatarioCodigoMunicipio; }
    public void setDestinatarioCodigoMunicipio(String destinatarioCodigoMunicipio) { this.destinatarioCodigoMunicipio = destinatarioCodigoMunicipio; }

    public String getDestinatarioMunicipio() { return destinatarioMunicipio; }
    public void setDestinatarioMunicipio(String destinatarioMunicipio) { this.destinatarioMunicipio = destinatarioMunicipio; }

    public String getDestinatarioUF() { return destinatarioUF; }
    public void setDestinatarioUF(String destinatarioUF) { this.destinatarioUF = destinatarioUF; }

    public String getDestinatarioCEP() { return destinatarioCEP; }
    public void setDestinatarioCEP(String destinatarioCEP) { this.destinatarioCEP = destinatarioCEP; }

    public List<ItemDCe> getItens() { return itens; }
    public void setItens(List<ItemDCe> itens) { this.itens = itens; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public Integer getModalidadeFrete() { return modalidadeFrete; }
    public void setModalidadeFrete(Integer modalidadeFrete) { this.modalidadeFrete = modalidadeFrete; }

    public String getCodigoRastreio() { return codigoRastreio; }
    public void setCodigoRastreio(String codigoRastreio) { this.codigoRastreio = codigoRastreio; }

    public String getModalidadePostagem() { return modalidadePostagem; }
    public void setModalidadePostagem(String modalidadePostagem) { this.modalidadePostagem = modalidadePostagem; }

    public BigDecimal getPesoTotal() { return pesoTotal; }
    public void setPesoTotal(BigDecimal pesoTotal) { this.pesoTotal = pesoTotal; }

    public String getObjetoPostal() { return objetoPostal; }
    public void setObjetoPostal(String objetoPostal) { this.objetoPostal = objetoPostal; }
}
