/*******************************************************************************
 * Copyright (c) 2020 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.translator.exception;

/**
 * {@link TranslatorRuntimeException} to {@code throw} when no {@link org.eclipse.kapua.translator.Translator} are available for the given {@link org.eclipse.kapua.message.Message} classes.
 *
 * @since 1.2.0
 */
public class TranslatorNotFoundException extends TranslatorRuntimeException {

    private final Class<?> fromMessageClass;
    private final Class<?> toMessageClass;

    public TranslatorNotFoundException(Class<?> fromMessageClass, Class<?> toMessageClass) {
        super(TranslatorErrorCodes.TRANSLATOR_NOT_FOUND, fromMessageClass, toMessageClass);

        this.fromMessageClass = fromMessageClass;
        this.toMessageClass = toMessageClass;
    }

    public Class<?> getFromMessageClass() {
        return fromMessageClass;
    }

    public Class<?> getToMessageClass() {
        return toMessageClass;
    }
}
