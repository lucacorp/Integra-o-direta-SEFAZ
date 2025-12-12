package com.exemplo.controlemesas.nfe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de XML NFCe contra schema XSD da SEFAZ.
 */
@Slf4j
@Service
public class NfeXmlValidator {

    /**
     * Valida XML da NFCe contra o schema XSD.
     *
     * @param xmlContent Conteúdo XML da NFCe
     * @return true se válido, false se inválido
     */
    public boolean validarXml(String xmlContent) {
        try {
            log.info("🔍 Iniciando validação local do XML NFCe contra schema XSD");
            
            // Schema NFCe 4.00
            String xsdPath = "schemas/nfe_v4.00.xsd";
            
            // Tenta carregar do classpath primeiro
            var resource = getClass().getClassLoader().getResource(xsdPath);
            
            if (resource == null) {
                log.warn("⚠️ Schema XSD não encontrado em resources. Validação local desabilitada.");
                log.warn("Para habilitar validação local, baixe os schemas XSD da SEFAZ:");
                log.warn("http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=/fmcTY5E5bzM=");
                return true; // Assume válido se não tem schema
            }
            
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(resource);
            Validator validator = schema.newValidator();
            
            // Lista para coletar erros
            List<String> erros = new ArrayList<>();
            
            validator.setErrorHandler(new org.xml.sax.ErrorHandler() {
                @Override
                public void warning(org.xml.sax.SAXParseException exception) {
                    log.warn("⚠️ Warning validação: {}", exception.getMessage());
                }
                
                @Override
                public void error(org.xml.sax.SAXParseException exception) {
                    String erro = String.format("Linha %d, Coluna %d: %s",
                            exception.getLineNumber(),
                            exception.getColumnNumber(),
                            exception.getMessage());
                    erros.add(erro);
                    log.error("❌ Erro validação: {}", erro);
                }
                
                @Override
                public void fatalError(org.xml.sax.SAXParseException exception) throws SAXException {
                    String erro = String.format("FATAL - Linha %d, Coluna %d: %s",
                            exception.getLineNumber(),
                            exception.getColumnNumber(),
                            exception.getMessage());
                    erros.add(erro);
                    log.error("💥 Erro fatal validação: {}", erro);
                    throw exception;
                }
            });
            
            validator.validate(new StreamSource(
                    new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))
            ));
            
            if (erros.isEmpty()) {
                log.info("✅ XML NFCe VÁLIDO contra schema XSD!");
                return true;
            } else {
                log.error("❌ XML NFCe INVÁLIDO! Erros encontrados:");
                erros.forEach(erro -> log.error("  - {}", erro));
                return false;
            }
            
        } catch (SAXException e) {
            log.error("❌ Erro de validação XML: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("❌ Erro ao validar XML: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Valida arquivo XML.
     *
     * @param xmlFilePath Caminho do arquivo XML
     * @return true se válido, false se inválido
     */
    public boolean validarArquivo(String xmlFilePath) {
        try {
            File arquivo = new File(xmlFilePath);
            if (!arquivo.exists()) {
                log.error("❌ Arquivo não encontrado: {}", xmlFilePath);
                return false;
            }
            
            String conteudo = java.nio.file.Files.readString(arquivo.toPath(), StandardCharsets.UTF_8);
            return validarXml(conteudo);
            
        } catch (Exception e) {
            log.error("❌ Erro ao ler arquivo: {}", e.getMessage(), e);
            return false;
        }
    }
}
