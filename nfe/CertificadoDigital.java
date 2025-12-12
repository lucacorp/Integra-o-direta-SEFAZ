package com.exemplo.controlemesas.nfe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * Gerencia o certificado digital A1 (PFX/P12) para assinatura de NF-e.
 */
@Slf4j
@Component
public class CertificadoDigital {

    private KeyStore keyStore;
    private PrivateKey privateKey;
    private X509Certificate certificate;
    private String alias;
    private String senha;

    /**
     * Carrega o certificado digital do arquivo PFX/P12.
     *
     * @param caminhoArquivo Caminho do arquivo .pfx ou .p12
     * @param senha Senha do certificado
     */
    public void carregar(String caminhoArquivo, String senha) throws Exception {
        log.info("Carregando certificado digital: {}", caminhoArquivo);
        
        this.senha = senha;
        keyStore = KeyStore.getInstance("PKCS12");
        
        try (FileInputStream fis = new FileInputStream(caminhoArquivo)) {
            keyStore.load(fis, senha.toCharArray());
        }

        // Encontra o alias do certificado
        Enumeration<String> aliases = keyStore.aliases();
        if (!aliases.hasMoreElements()) {
            throw new IllegalStateException("Nenhum certificado encontrado no arquivo.");
        }

        alias = aliases.nextElement();
        log.debug("Alias do certificado: {}", alias);

        // Extrai chave privada e certificado
        privateKey = (PrivateKey) keyStore.getKey(alias, senha.toCharArray());
        certificate = (X509Certificate) keyStore.getCertificate(alias);

        if (privateKey == null || certificate == null) {
            throw new IllegalStateException("Falha ao extrair chave privada ou certificado.");
        }

        // Verifica validade do certificado
        certificate.checkValidity();
        
        log.info("Certificado carregado com sucesso. Titular: {}", certificate.getSubjectX500Principal().getName());
        log.info("Válido até: {}", certificate.getNotAfter());
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            throw new IllegalStateException("Certificado não foi carregado. Chame carregar() primeiro.");
        }
        return privateKey;
    }

    public X509Certificate getCertificate() {
        if (certificate == null) {
            throw new IllegalStateException("Certificado não foi carregado. Chame carregar() primeiro.");
        }
        return certificate;
    }

    public KeyStore getKeyStore() {
        if (keyStore == null) {
            throw new IllegalStateException("Certificado não foi carregado. Chame carregar() primeiro.");
        }
        return keyStore;
    }

    public String getAlias() {
        return alias;
    }

    public String getSenha() {
        if (senha == null) {
            throw new IllegalStateException("Certificado não foi carregado. Chame carregar() primeiro.");
        }
        return senha;
    }

    public boolean isCarregado() {
        return certificate != null && privateKey != null;
    }
}
