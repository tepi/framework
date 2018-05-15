/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui.renderers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import com.vaadin.server.SerializableSupplier;
import com.vaadin.shared.ui.grid.renderers.LocalDateTimeRendererState;

import elemental.json.JsonValue;
import static com.vaadin.ui.renderers.LocalDateRenderer.checkSerialization;

/**
 * A renderer for presenting {@code LocalDateTime} objects.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class LocalDateTimeRenderer
        extends AbstractRenderer<Object, LocalDateTime> {

    private SerializableSupplier<DateTimeFormatter> formatterSupplier;
    private boolean getLocaleFromGrid;

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the grid's locale it is
     * attached to, with the format style being {@code FormatStyle.LONG} for the
     * date and {@code FormatStyle.SHORT} for time, with an empty string as its
     * null representation.
     *
     * @see <a href=
     *      "https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html#LONG">
     *      FormatStyle.LONG</a>
     * @see <a href=
     *      "https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html#SHORT">
     *      FormatStyle.SHORT</a>
     */
    public LocalDateTimeRenderer() {
        this(() -> DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG,
                FormatStyle.SHORT), "");
        getLocaleFromGrid = true;
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given formatter, with the
     * empty string as its null representation.
     *
     * <p>
     * <b>Note</b> the {@code DateTimeFormatter} is not a serializable class, so
     * using this method in an environment which requires session persistence
     * may produce {@link java.io.NotSerializableException}.
     *
     * @param formatter
     *            the formatter to use, not {@code null}
     *
     * @throws IllegalArgumentException
     *             if formatter is null
     * @deprecated the method is unsafe for serialization, may produce troubles
     *             in a cluster environment
     * @see #LocalDateTimeRenderer(SerializableSupplier)
     */
    @Deprecated
    public LocalDateTimeRenderer(DateTimeFormatter formatter) {
        this(formatter, "");
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given formatter.
     *
     * <p>
     * <b>Note</b> the {@code DateTimeFormatter} is not a serializable class, so
     * using this method in an environment which requires session persistence
     * may produce {@link java.io.NotSerializableException}.
     *
     *
     * @param formatter
     *            the formatter to use, not {@code null}
     * @param nullRepresentation
     *            the textual representation of the {@code null} value
     *
     * @throws IllegalArgumentException
     *             if formatter is null
     * @deprecated the method is unsafe for serialization, may produce troubles
     *             in acluster environment
     * @see #LocalDateTimeRenderer(SerializableSupplier, String)
     */
    @Deprecated
    public LocalDateTimeRenderer(DateTimeFormatter formatter,
            String nullRepresentation) {
        super(LocalDateTime.class, nullRepresentation);

        if (formatter == null) {
            throw new IllegalArgumentException("formatter may not be null");
        }

        this.formatterSupplier = () -> formatter;
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given string format, as
     * displayed in the grid's locale it is attached to, with an empty string as
     * its null representation.
     *
     * @param formatPattern
     *            the format pattern to format the date with, not {@code null}
     *
     * @throws IllegalArgumentException
     *             if format pattern is null
     *
     * @see <a href=
     *      "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns">
     *      Format Pattern Syntax</a>
     */
    public LocalDateTimeRenderer(String formatPattern) {
        this(formatPattern, Locale.getDefault());
        getLocaleFromGrid = true;
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given string format, as
     * displayed in the given locale, with an empty string as its null
     * representation.
     *
     * @param formatPattern
     *            the format pattern to format the date with, not {@code null}
     * @param locale
     *            the locale to use, not {@code null}
     *
     * @throws IllegalArgumentException
     *             if format pattern is null
     * @throws IllegalArgumentException
     *             if locale is null
     *
     * @see <a href=
     *      "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns">
     *      Format Pattern Syntax</a>
     */
    public LocalDateTimeRenderer(String formatPattern, Locale locale) {
        this(formatPattern, locale, "");
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given string format, as
     * displayed in the given locale.
     *
     * @param formatPattern
     *            the format pattern to format the date with, not {@code null}
     * @param locale
     *            the locale to use, not {@code null}
     * @param nullRepresentation
     *            the textual representation of the {@code null} value
     *
     * @throws IllegalArgumentException
     *             if format pattern is null
     * @throws IllegalArgumentException
     *             if locale is null
     *
     * @see <a href=
     *      "https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns">
     *      Format Pattern Syntax</a>
     */
    public LocalDateTimeRenderer(String formatPattern, Locale locale,
            String nullRepresentation) {
        super(LocalDateTime.class, nullRepresentation);

        if (formatPattern == null) {
            throw new IllegalArgumentException(
                    "format pattern may not be null");
        }

        if (locale == null) {
            throw new IllegalArgumentException("locale may not be null");
        }

        formatterSupplier = () -> DateTimeFormatter.ofPattern(formatPattern,
                locale);
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given formatterSupplier.
     *
     * @param formatterSupplier
     *            the formatterSupplier supplier to use, not {@code null}, it
     *            should not supply {@code null} either
     * @throws IllegalArgumentException
     *             if formatterSupplier is null
     */
    public LocalDateTimeRenderer(
            SerializableSupplier<DateTimeFormatter> formatterSupplier) {
        this(formatterSupplier, "");
    }

    /**
     * Creates a new LocalDateTimeRenderer.
     * <p>
     * The renderer is configured to render with the given formatterSupplier.
     *
     * @param formatterSupplier
     *            the formatterSupplier supplier to use, not {@code null}, it
     *            should not supply {@code null} either
     * @param nullRepresentation
     *            the textual representation of the {@code null} value
     *
     * @throws IllegalArgumentException
     *             if formatterSupplier is null
     */
    public LocalDateTimeRenderer(
            SerializableSupplier<DateTimeFormatter> formatterSupplier,
            String nullRepresentation) {
        super(LocalDateTime.class, nullRepresentation);

        if (formatterSupplier == null) {
            throw new IllegalArgumentException(
                    "formatterSupplier may not be null");
        }
        this.formatterSupplier = formatterSupplier;
        assert checkSerialization(formatterSupplier);
    }

    @Override
    public JsonValue encode(LocalDateTime value) {
        String dateString;
        if (value == null) {
            dateString = getNullRepresentation();
        } else if (getLocaleFromGrid) {
            if (getParentGrid() == null) {
                throw new IllegalStateException(
                        "Could not find a locale to format with: "
                                + "this renderer should either be attached to a grid "
                                + "or constructed with locale information");
            }
            dateString = value.format(formatterSupplier.get()
                    .withLocale(getParentGrid().getLocale()));
        } else {
            dateString = value.format(formatterSupplier.get());
        }
        return encode(dateString, String.class);
    }

    @Override
    protected LocalDateTimeRendererState getState() {
        return (LocalDateTimeRendererState) super.getState();
    }

    @Override
    protected LocalDateTimeRendererState getState(boolean markAsDirty) {
        return (LocalDateTimeRendererState) super.getState(markAsDirty);
    }
}
