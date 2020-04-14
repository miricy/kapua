/*******************************************************************************
 * Copyright (c) 2017, 2020 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.translator.kapua.kura;

import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.app.request.KuraRequestPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.exception.InvalidMessageException;
import org.eclipse.kapua.translator.exception.InvalidPayloadException;
import org.eclipse.kapua.translator.exception.TranslateException;

/**
 * {@link Translator} abstract implementation from {@link KapuaMessage} to {@link KuraRequestMessage}
 *
 * @since 1.0.0
 */
public abstract class AbstractTranslatorKapuaKura<FROM_C extends KapuaChannel, FROM_P extends KapuaPayload, FROM_M extends KapuaMessage<FROM_C, FROM_P>> extends Translator<FROM_M, KuraRequestMessage> {

    private static final String CONTROL_MESSAGE_CLASSIFIER = SystemSetting.getInstance().getMessageClassifier();

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AccountService ACCOUNT_SERVICE = LOCATOR.getService(AccountService.class);
    private static final DeviceRegistryService DEVICE_REGISTRY_SERVICE = LOCATOR.getService(DeviceRegistryService.class);

    @Override
    public KuraRequestMessage translate(FROM_M kapuaMessage) throws TranslateException {
        try {
            Account account = KapuaSecurityUtils.doPrivileged(() -> ACCOUNT_SERVICE.find(kapuaMessage.getScopeId()));

            Device device = null;
            if (kapuaMessage.getDeviceId() != null) {
                device = DEVICE_REGISTRY_SERVICE.find(kapuaMessage.getScopeId(), kapuaMessage.getDeviceId());
            }

            KuraRequestChannel kuraRequestChannel = translateChannel(kapuaMessage.getChannel());
            kuraRequestChannel.setScope(account.getName());
            kuraRequestChannel.setClientId(device != null ? device.getClientId() : kapuaMessage.getClientId());

            //
            // Kura payload
            KuraRequestPayload kuraPayload = translatePayload(kapuaMessage.getPayload());

            //
            // Return Kura Message
            return new KuraRequestMessage(kuraRequestChannel, kapuaMessage.getReceivedOn(), kuraPayload);
        } catch (InvalidChannelException | InvalidPayloadException te) {
            throw te;
        } catch (Exception e) {
            throw new InvalidMessageException(e, kapuaMessage);
        }
    }

    /**
     * Gets the value from {@link SystemSetting#getMessageClassifier()}
     *
     * @return The value from {@link SystemSetting#getMessageClassifier()}
     * @since 1.2.0
     */
    protected static String getControlMessageClassifier() {
        return CONTROL_MESSAGE_CLASSIFIER;
    }

    protected abstract KuraRequestChannel translateChannel(FROM_C kapuaChannel) throws InvalidChannelException;

    protected abstract KuraRequestPayload translatePayload(FROM_P kapuaPayload) throws InvalidPayloadException;

}
