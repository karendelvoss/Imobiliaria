package dto;

/**
 * DTO (Data Transfer Object) para carregar os dados consolidados dos participantes de um contrato.
 * Facilita o transporte de informações entre a camada DAO e a camada de Serviço/Apresentação.
 */
public class ParticipantDTO {
    private String nomeRazaoSocial;
    private String cpfCnpj;
    private String papelRole;
    private double percentualParticipacao;
    private String contatoPrincipal;
    private String statusAssinatura;

    // Getters e Setters
    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }
    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }
    public String getCpfCnpj() {
        return cpfCnpj;
    }
    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
    public String getPapelRole() {
        return papelRole;
    }
    public void setPapelRole(String papelRole) {
        this.papelRole = papelRole;
    }
    public double getPercentualParticipacao() {
        return percentualParticipacao;
    }
    public void setPercentualParticipacao(double percentualParticipacao) {
        this.percentualParticipacao = percentualParticipacao;
    }
    public String getContatoPrincipal() {
        return contatoPrincipal;
    }
    public void setContatoPrincipal(String contatoPrincipal) {
        this.contatoPrincipal = contatoPrincipal;
    }
    public String getStatusAssinatura() {
        return statusAssinatura;
    }
    public void setStatusAssinatura(String statusAssinatura) {
        this.statusAssinatura = statusAssinatura;
    }
}