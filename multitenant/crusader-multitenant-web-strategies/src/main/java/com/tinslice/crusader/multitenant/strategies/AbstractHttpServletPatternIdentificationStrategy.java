package com.tinslice.crusader.multitenant.strategies;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class used to identify tenant based on regular expression.
 */
public abstract class AbstractHttpServletPatternIdentificationStrategy implements TenantIdentificationStrategy {
    public static final String PARAM_PATTERN = "regexp";
    public static final String PARAM_PATTERN_MATCH_POSITION = "matchIndex";

    private Pattern pattern;
    private int matchPosition;

    protected Pattern getPattern() {
        return pattern;
    }

    protected void setPattern(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern must not be null.");
        }
        this.pattern = pattern;
    }

    protected int getMatchPosition() {
        return matchPosition;
    }

    protected void setMatchPosition(int matchPosition) {
        this.matchPosition = matchPosition;
    }

    protected abstract String matcherInput(HttpServletRequest request);

    @Override
    public Object identifyTenant(HttpServletRequest request) {
        Matcher matcher = getPattern().matcher(matcherInput(request));
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(getMatchPosition());
    }

    @Override
    public void initialize(Map<String, Object> params) {
        String pattern = "";

        if (params != null) {
            if (params.containsKey(PARAM_PATTERN)) {
                pattern = (String) params.get(PARAM_PATTERN);
            }
            if (params.containsKey(PARAM_PATTERN_MATCH_POSITION)) {
                setMatchPosition((Integer) params.get(PARAM_PATTERN_MATCH_POSITION));
            }
        }

        pattern = pattern.trim();
        setPattern(pattern.length() == 0 ? null : Pattern.compile(pattern));
    }
}
