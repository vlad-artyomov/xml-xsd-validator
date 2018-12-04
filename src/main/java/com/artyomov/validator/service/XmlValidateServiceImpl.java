package com.artyomov.validator.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
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

    private Schema xsdSchema;

    @PostConstruct
    private void init() throws IOException, SAXException {
        Resource xsdResource = new ClassPathResource("sample.xsd");
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        xsdSchema = factory.newSchema(new StreamSource(xsdResource.getInputStream()));
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