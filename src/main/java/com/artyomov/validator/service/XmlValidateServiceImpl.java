package com.artyomov.validator.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;

/**
 * @author <a href="mailto:artyomov.dev@gmail.com">Vlad Artyomov</a>
 * Date: 05/12/2018
 * Time: 00:17
 */
@Service
public class XmlValidateServiceImpl implements XmlValidateService {

    @Autowired
    private Logger logger;

    @Value("${reference.xsd.path}")
    private String refXsdPath;

    private Schema xsdSchema;

    @PostConstruct
    private void init() throws SAXException {
        File xsdSchemaFile = new File(refXsdPath);
        if (xsdSchemaFile.isDirectory()) {
            File[] xsdFiles = xsdSchemaFile.listFiles();
            if (xsdFiles != null && xsdFiles.length > 0) {
                xsdSchemaFile = xsdFiles[0];
            } else {
                throw new RuntimeException("XSD folder is empty");
            }
        }
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        xsdSchema = factory.newSchema(xsdSchemaFile);
        logger.info("Loaded {} as a reference XSD", xsdSchemaFile.getName());
    }

    @Override
    public Mono<Boolean> validateXml(String sourceXml) {
        boolean checkingResult = false;
        try {
            Validator validator = xsdSchema.newValidator();
            validator.validate(new StreamSource(new StringReader(sourceXml)));
            checkingResult = true;
        } catch (Exception ex) {
            logger.warn("XML validation failed: {}", ex.getMessage());
        }
        return Mono.just(checkingResult);
    }
}