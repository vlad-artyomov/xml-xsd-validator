package com.artyomov.validator.controller;

import com.artyomov.validator.service.XmlValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:artyomov.dev@gmail.com">Vlad Artyomov</a>
 * Date: 05/12/2018
 * Time: 00:14
 */
@RestController
@RequestMapping(value = "/api/validate")
public class XmlValidateResource {

    @Autowired
    private XmlValidateService xmlValidateService;

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public Mono<ResponseEntity<Boolean>> validateXml(@RequestBody String sourceXml) {
        return xmlValidateService.validateXml(sourceXml).map(ResponseEntity::ok);
    }
}