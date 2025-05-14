package com.csniico.notificationService.SNS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SetSubscriptionAttributesRequest;

public class SNSMessageFilterPolicy {
    private final Map<String, Attribute> filterPolicy = new HashMap<>();

    public void addAttribute(final String attributeName, final String attributeValue) {
        filterPolicy.put(attributeName, new Attribute<>(AttributeType.String, attributeValue));
    }

    public void addAttribute(final String attributeName, final ArrayList<String> attributeValues) {
        final ArrayList<Attribute> attributes = new ArrayList<>();
        for (final String s : attributeValues) {
            attributes.add(new Attribute<>(AttributeType.String, s));
        }
        filterPolicy.put(attributeName, new Attribute<>(AttributeType.List, attributes));
    }

    public void addAttributePrefix(final String attributeName, final String prefix) {
        filterPolicy.put(attributeName, new Attribute<>(AttributeType.Prefix, prefix));
    }

    public void addAttributeAnythingBut(final String attributeName, final String value) {
        filterPolicy.put(attributeName, new Attribute<>(AttributeType.AnythingBut, value));
    }

    public <T extends Number> void addAttribute(final String attributeName, final String op, final T value) {
        filterPolicy.put(attributeName, new Attribute<>(AttributeType.Numeric, new NumericValue<>(op, value)));
    }

    public <T extends Number> void addAttributeRange(
            final String attributeName,
            final String lowerOp,
            final T lower,
            final String upperOp,
            final T upper) {
        filterPolicy.put(attributeName,
                new Attribute<>(AttributeType.Numeric, new NumericValue<>(lowerOp, lower, upperOp, upper)));
    }

    // This method produces a policy string from the contents of the filterPolicy map
    // and then applies the policy to the specified subscription.
    public void apply(SnsClient snsClient, String subscriptionArn) {

        SetSubscriptionAttributesRequest request = SetSubscriptionAttributesRequest.builder()
                .subscriptionArn(subscriptionArn)
                .attributeName("FilterPolicy")
                .attributeValue(formatFilterPolicy())
                .build();

        snsClient.setSubscriptionAttributes(request);
    }

    private String formatFilterPolicy() {
        return filterPolicy.entrySet()
                .stream()
                .map(entry -> "\"" + entry.getKey() + "\": [" + entry.getValue() + "]")
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private enum AttributeType {
        String, Numeric, Prefix, List, AnythingBut
    }

    private class Attribute<T> {
        final T value;
        final AttributeType type;

        Attribute(final AttributeType type, final T value) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            switch (type) {
                case Prefix:
                    return String.format("{\"prefix\":\"%s\"}", value.toString());
                case Numeric:
                    return String.format("{\"numeric\":%s}", value.toString());
                case List:
                    final List list = (List)value;
                    final ArrayList<T> values = new ArrayList<T>(list);
                    return values
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                case AnythingBut:
                    return String.format("{\"anything-but\":\"%s\"}", value);
                default:
                    return String.format("\"%s\"", value);
            }
        }
    }

    private class NumericValue<T extends Number> {
        private final T lower;
        private final T upper;
        private final String lowerOp;
        private final String upperOp;

        NumericValue(final String op, final T value) {
            lower = value;
            lowerOp = op;
            upper = null;
            upperOp = null;
        }

        NumericValue(final String lowerOp, final T lower, final String upperOp, final T upper) {
            this.lower = lower;
            this.lowerOp = lowerOp;
            this.upper = upper;
            this.upperOp = upperOp;
        }

        public String toString() {
            final StringBuilder s = new StringBuilder("[")
                    .append('\"')
                    .append(lowerOp)
                    .append("\",")
                    .append(lower);
            if (upper != null) {
                s.append(",\"")
                        .append(upperOp).
                        append("\",").
                        append(upper);
            }
            s.append("]");
            return s.toString();
        }
    }
}
