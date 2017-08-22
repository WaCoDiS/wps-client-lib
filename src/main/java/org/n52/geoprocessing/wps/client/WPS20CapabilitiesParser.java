/*
 * ﻿Copyright (C) ${inceptionYear} - ${currentYear} 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.geoprocessing.wps.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.Address;
import org.n52.geoprocessing.wps.client.model.ContactInfo;
import org.n52.geoprocessing.wps.client.model.Phone;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.ServiceContact;
import org.n52.geoprocessing.wps.client.model.ServiceIdentification;
import org.n52.geoprocessing.wps.client.model.ServiceProvider;
import org.n52.geoprocessing.wps.client.model.WPSCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.opengis.ows.x20.AddressType;
import net.opengis.ows.x20.ContactType;
import net.opengis.ows.x20.LanguageStringType;
import net.opengis.ows.x20.ResponsiblePartySubsetType;
import net.opengis.ows.x20.TelephoneType;
import net.opengis.wps.x20.CapabilitiesDocument;
import net.opengis.wps.x20.ProcessSummaryType;

public class WPS20CapabilitiesParser {

    private static Logger LOGGER = LoggerFactory.getLogger(WPS20CapabilitiesParser.class);

    public WPS20CapabilitiesParser() {}

    public WPSCapabilities createWPSCapabilitiesOWS20(CapabilitiesDocument xmlObject) {

        LOGGER.trace("Parsing capabilities: " + xmlObject);

        WPSCapabilities result = new WPSCapabilities();

        net.opengis.ows.x20.ServiceIdentificationDocument.ServiceIdentification xmlServiceIdentification =
                xmlObject.getCapabilities().getServiceIdentification();

        ServiceIdentification serviceIdentification = createServiceIdentification(xmlServiceIdentification);

        result.setServiceIdentification(serviceIdentification);

        net.opengis.ows.x20.ServiceProviderDocument.ServiceProvider xmlServiceProvider =
                xmlObject.getCapabilities().getServiceProvider();

        ServiceProvider serviceProvider = createServiceProvider(xmlServiceProvider);

        result.setServiceProvider(serviceProvider);

        List<Process> processes = createProcesses(xmlObject.getCapabilities().getContents().getProcessSummaryArray());

        result.setProcesses(processes);

        return result;
    }

    private ServiceIdentification createServiceIdentification(
            net.opengis.ows.x20.ServiceIdentificationDocument.ServiceIdentification xmlServiceIdentification) {

        ServiceIdentification serviceIdentification = new ServiceIdentification();

        try {
            serviceIdentification.setTitle(xmlServiceIdentification.getTitleArray(0).getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not get title from capabilities.", e);
        }

        LanguageStringType abstrakt = xmlServiceIdentification.getAbstractArray(0);

        if (abstrakt != null) {
            serviceIdentification.setAbstract(abstrakt.getStringValue());

        }

        try {
            String[] serviceTypeVersions = xmlServiceIdentification.getServiceTypeVersionArray();

            List<String> serviceTypeVersionList = Arrays.asList(serviceTypeVersions);

            serviceIdentification.setServiceTypeVersions(serviceTypeVersionList);

        } catch (Exception e) {
            LOGGER.info("Could not get service type versions from capabilities.", e);
        }

        if(xmlServiceIdentification.getKeywordsArray() != null && xmlServiceIdentification.getKeywordsArray().length > 0){
            try {
                LanguageStringType[] keyWordArray = xmlServiceIdentification.getKeywordsArray(0).getKeywordArray();

                List<String> keyWordList = new ArrayList<>();

                for (LanguageStringType keyword : keyWordArray) {
                    keyWordList.add(keyword.getStringValue());
                }

                serviceIdentification.setKeyWords(keyWordList);
            } catch (Exception e) {
                LOGGER.info("Could not get keywords from capabilities.", e);
            }
        }else{
            LOGGER.info("Could not get keywords from capabilities, element is null.");
        }

        try {
            serviceIdentification.setFees(xmlServiceIdentification.getFees());
        } catch (Exception e) {
            LOGGER.info("Could not get fees from capabilities.", e);
        }

        try {
            String[] accessConstraints = xmlServiceIdentification.getAccessConstraintsArray();

            List<String> accessConstraintList = Arrays.asList(accessConstraints);

            serviceIdentification.setAccessConstraints(accessConstraintList);

        } catch (Exception e) {
            LOGGER.info("Could not get service type versions from capabilities.", e);
        }

        return serviceIdentification;
    }

    private ServiceProvider createServiceProvider(
            net.opengis.ows.x20.ServiceProviderDocument.ServiceProvider xmlServiceProvider) {
        ServiceProvider serviceProvider = new ServiceProvider();

        try {
            serviceProvider.setProviderName(xmlServiceProvider.getProviderName());
        } catch (Exception e) {
            LOGGER.info("Could not get provider name from capabilities.", e);
        }

        try {
            serviceProvider.setProviderSite(xmlServiceProvider.getProviderSite().getHref());
        } catch (Exception e) {
            LOGGER.info("Could not get provider site from capabilities.", e);
        }

        try {
            serviceProvider.setServiceContact(createServiceContact(xmlServiceProvider.getServiceContact()));
        } catch (Exception e) {
            LOGGER.info("Could not get service contact from capabilities.", e);
        }

        return serviceProvider;
    }

    private ServiceContact createServiceContact(ResponsiblePartySubsetType xmlServiceContact) {
        ServiceContact serviceContact = new ServiceContact();

        try {
            serviceContact.setIndividualName(xmlServiceContact.getIndividualName());
        } catch (Exception e) {
            LOGGER.info("Could not get individual name from capabilities.", e);
        }

        try {
            serviceContact.setPositionName(xmlServiceContact.getPositionName());
        } catch (Exception e) {
            LOGGER.info("Could not get position name from capabilities.", e);
        }

        try {
            serviceContact.setContactInfo(createContactInfo(xmlServiceContact.getContactInfo()));
        } catch (Exception e) {
            LOGGER.info("Could not get contact info from capabilities.", e);
        }

        return serviceContact;
    }

    private ContactInfo createContactInfo(ContactType xmlContactInfo) {
        ContactInfo contactInfo = new ContactInfo();

        try {
            contactInfo.setPhone(createPhone(xmlContactInfo.getPhone()));
        } catch (Exception e) {
            LOGGER.info("Could not get phone from capabilities.", e);
        }

        try {
            contactInfo.setAddress(createAddress(xmlContactInfo.getAddress()));
        } catch (Exception e) {
            LOGGER.info("Could not get address from capabilities.", e);
        }

        return contactInfo;
    }

    private Phone createPhone(TelephoneType xmlPhone) {

        Phone phone = new Phone();

        if(xmlPhone == null){
            return phone;
        }

        if(xmlPhone.getVoiceArray() != null && xmlPhone.getVoiceArray().length > 0){
            try {
                phone.setVoice(xmlPhone.getVoiceArray(0));
            } catch (Exception e) {
                LOGGER.info("Could not get voice from capabilities.", e);
            }
        }else{
            LOGGER.info("Could not get voice from capabilities, element is emtpy.");
        }

        if(xmlPhone.getFacsimileArray() != null && xmlPhone.getFacsimileArray().length > 0){
            try {
                phone.setFacsimile(xmlPhone.getFacsimileArray(0));
            } catch (Exception e) {
                LOGGER.info("Could not get facsimile from capabilities.", e);
            }
        }else{
            LOGGER.info("Could not get facsimile from capabilities, element is emtpy.");
        }

        return phone;
    }

    private Address createAddress(AddressType xmlAddress) {
        Address address = new Address();

        try {
            address.setAdministrativeArea(xmlAddress.getAdministrativeArea());
        } catch (Exception e) {
            LOGGER.info("Could not get administrative area from capabilities.", e);
        }

        try {
            address.setCity(xmlAddress.getCity());
        } catch (Exception e) {
            LOGGER.info("Could not get city from capabilities.", e);
        }

        try {
            address.setCountry(xmlAddress.getCountry());
        } catch (Exception e) {
            LOGGER.info("Could not get country from capabilities.", e);
        }

        try {
            address.setDeliveryPoint(xmlAddress.getDeliveryPointArray(0));
        } catch (Exception e) {
            LOGGER.info("Could not get delivery point from capabilities.", e);
        }

        try {
            address.setElectronicMailAddress(xmlAddress.getElectronicMailAddressArray(0));
        } catch (Exception e) {
            LOGGER.info("Could not get electronic mail address from capabilities.", e);
        }

        try {
            address.setPostalCode(xmlAddress.getPostalCode());
        } catch (Exception e) {
            LOGGER.info("Could not get postal code from capabilities.", e);
        }

        return address;
    }

    private List<Process> createProcesses(ProcessSummaryType[] processSummaryTypes) {
        List<Process> processes = new ArrayList<>();

        for (ProcessSummaryType processSummaryType : processSummaryTypes) {

            processes.add(createProcess(processSummaryType));

        }

        return processes;
    }

    private Process createProcess(ProcessSummaryType processSummaryType) {
        Process process = new Process();
        LOGGER.trace(processSummaryType.toString());

        try {
            process.setId(processSummaryType.getIdentifier().getStringValue());
        } catch (Exception e) {
            LOGGER.info("Could not get id from process.", e);
        }

        if(processSummaryType.getAbstractArray().length > 0){
            try {
                LanguageStringType abstrakt = processSummaryType.getAbstractArray(0);//TODO
                if (abstrakt != null) {
                    process.setAbstract(abstrakt.getStringValue());
                }
            } catch (Exception e) {
                LOGGER.info("Could not get abstract from process.", e);
            }
        }else{
            LOGGER.info("Could not get abstract from process, element is emtpy.");
        }

        if(processSummaryType.getTitleArray().length > 0){
            try {
                process.setTitle(processSummaryType.getTitleArray(0).getStringValue());
            } catch (Exception e) {
                LOGGER.info("Could not get title from process.", e);
            }
        }else{
            LOGGER.info("Could not get title from process, element is emtpy.");
        }

        List<?> jobControlOptions = processSummaryType.getJobControlOptions();

        for (Iterator<?> iterator = jobControlOptions.iterator(); iterator.hasNext();) {
            String jobControlOption = (String) iterator.next();
            if (jobControlOption.equals("async-execute")) {
                process.setStatusSupported(true);
                break;
            }
        }

        List<?> transmissionModes = processSummaryType.getOutputTransmission();

        for (Iterator<?> iterator = transmissionModes.iterator(); iterator.hasNext();) {
            String transmissionMode = (String) iterator.next();
            if (transmissionMode.equals("reference")) {
                process.setReferenceSupported(true);
                break;
            }
        }

        return process;
    }

}