package com.artyomov.validator.service;

import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:artyomov.dev@gmail.com">Vlad Artyomov</a>
 * Date: 05/12/2018
 * Time: 00:27
 */
public interface XmlValidateService {

    Mono<Boolean> validateXml(String sourceXml);
}
