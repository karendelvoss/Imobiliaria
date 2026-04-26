package dto;

/**
 * Objeto de transferência de dados para carregar informações consolidadas dos participantes de um contrato.
 */
public class ParticipantDTO {
    private String nomeRazaoSocial;
    private String cpfCnpj;
    private String papelRole;
    private double percentualParticipacao;
    private String contatoPrincipal;
    private String statusAssinatura;

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