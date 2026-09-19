package com.fduenasc.domain.model;

/**
 * Stable message keys emitted by domain validation. The UI layer resolves them to visible text.
 *
 * @author Francisco Dueñas
 * @since 0.1.0
 */
public final class MessageKeys {

    public static final String JSON_DATA_MODEL_NULL_ROOT = "uitext.json.data_model_null_root";
    public static final String JSON_DATA_MODEL_NEED_OBJECT = "uitext.json.data_model_need_object";
    public static final String JSON_PARSE_FALLBACK = "uitext.json.parse_fallback";

    /**
     * Private constructor to prevent instantiation.
     */
    private MessageKeys() {
    }
}
