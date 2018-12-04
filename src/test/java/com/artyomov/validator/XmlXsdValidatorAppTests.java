package com.artyomov.validator;

import com.artyomov.validator.service.XmlValidateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XmlXsdValidatorAppTests {

    private static final int VALIDATE_RUNS = 1000;

    @Autowired
    private XmlValidateService xmlValidateService;

    @Autowired
    private Logger logger;

    @Test
    public void shouldValidateXml() throws IOException {
        final String validXml = readFile("src/test/resources/sample.xml");
        long averageTime = 0;
        for (int i = 0; i < VALIDATE_RUNS; i++) {
            averageTime += checkValidationTime(validXml);
        }
        logger.info("Average XML validation time is {} ms for {} runs", (double) averageTime / VALIDATE_RUNS, VALIDATE_RUNS);
    }

    private long checkValidationTime(String xmlToValidate) {
        long startTime = System.currentTimeMillis();
        xmlValidateService.validateXml(xmlToValidate).subscribe();
        long totalTime = System.currentTimeMillis() - startTime;
        logger.info("XML validation completed in {} ms", totalTime);
        return totalTime;
    }

    private String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }
}
