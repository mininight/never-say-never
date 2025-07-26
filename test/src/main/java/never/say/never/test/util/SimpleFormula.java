package never.say.never.test.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static never.say.never.test.util.SimpleFormula.Context.newStrBuilder;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-11
 */
@Slf4j
@Data
public class SimpleFormula {
    private static final int EXPR_LEN_LIMIT = 1000;
    private static final int DEFAULT_SCALE = 8;
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final char TEMP_VAR_NAME_OPEN = '<';
    private static final char TEMP_VAR_NAME_DELIMITER = '_';
    private static final char TEMP_VAR_NAME_CLOSE = '>';
    private RoundingMode roundingMode;
    private final Map<Character, Opt> supportedOpts = new HashMap<>();

    {
        acceptOpts(SimpleOpt.values());
    }

    public static SimpleFormula newInstance() {
        return new SimpleFormula();
    }

    public SimpleFormula acceptOpts(Opt... opts) {
        for (Opt opt : opts) {
            supportedOpts.put(opt.getSymbol(), opt);
            supportedOpts.putIfAbsent(opt.getCnSymbol(), opt);
        }
        return this;
    }

    public SimpleFormula acceptOpts(Collection<? extends Opt> opts) {
        for (Opt opt : opts) {
            supportedOpts.put(opt.getSymbol(), opt);
            supportedOpts.putIfAbsent(opt.getCnSymbol(), opt);
        }
        return this;
    }

    public BigDecimal calculate(String expression, Map<String, BigDecimal> variables) {
        return calculate(expression, variables, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public BigDecimal calculate(String expression, Map<String, BigDecimal> variables, int scale) {
        return calculate(expression, variables, scale, DEFAULT_ROUNDING_MODE);
    }

    public BigDecimal calculate(String expression, Map<String, BigDecimal> variables, RoundingMode roundingMode) {
        return calculate(expression, variables, DEFAULT_SCALE, roundingMode);
    }

    public BigDecimal calculate(String expression, Map<String, BigDecimal> variables, int scale, RoundingMode roundingMode) {
        if (roundingMode == null) {
            throw new CalculateException("Rounding mode not defined");
        }
        String ctxExpr = expression == null ? null : validateAndCleanExpr(expression);
        if (ctxExpr == null || ctxExpr.length() == 0) {
            throw new CalculateException(String.format("Invalid expression: %s", expression));
        }
        Map<String, BigDecimal> ctxVarsMap = new LinkedHashMap<>();
        int varMaxScale = 0;
        String varName;
        BigDecimal varValue;
        if (!variables.isEmpty()) {
            for (Map.Entry<String, BigDecimal> entry : variables.entrySet()) {
                varName = validateAndCleanVarName(entry.getKey());
                varValue = entry.getValue();
                if (varValue == null) {
                    throw new CalculateException(String.format("Variable '%s' value cannot be empty", varName));
                }
                varMaxScale = Math.max(varMaxScale, varValue.scale());
                ctxVarsMap.put(varName, varValue);
            }
        }
        int ctxScale = Math.max(varMaxScale, Math.max(scale, DEFAULT_SCALE)) * 2;
        try {
            Context.init(expression, ctxExpr, ctxVarsMap, ctxScale, roundingMode, supportedOpts);
            ctxExpr = formatExprAsSimple(ctxExpr, ctxVarsMap);
            return calculateSimpleExpr(ctxExpr, ctxVarsMap).setScale(scale < 0 ? ctxScale : scale, roundingMode);
        } finally {
            Context.destroy();
        }
    }

    protected String validateAndCleanExpr(String expression) {
        if (expression.length() > EXPR_LEN_LIMIT) {
            throw new CalculateException("Expression length exceeds limit");
        }
        StringBuilder cleanExprBuilder = newStrBuilder();
        for (char c : expression.toCharArray()) {
            if (isIgnoreChar(c)) {
                continue;
            }
            c = exchangeExprChar(c);
            if (!isValidExprChar(c)) {
                throw new CalculateException(String.format("Expression contains illegal character: '%s'", c));
            }
            cleanExprBuilder.append(c);
        }
        String cleanExpr = cleanExprBuilder.toString();
        cleanExprBuilder.setLength(0);
        return cleanExpr;
    }

    public String validateAndCleanVarName(String varName) {
        if (varName == null || varName.length() == 0) {
            throw new CalculateException("Variable name cannot be empty");
        }
        StringBuilder cleanVarNameBuilder = newStrBuilder();
        for (char c : varName.toCharArray()) {
            if (isIgnoreChar(c)) {
                continue;
            }
            if (isValidVarNameChar(c)) {
                cleanVarNameBuilder.append(c);
                continue;
            }
            throw new CalculateException(String.format("Variable name '%s', include an invalid char: %s", varName, c));
        }
        if (cleanVarNameBuilder.length() == 0) {
            throw new CalculateException(String.format("Invalid variable name '%s'", varName));
        }
        varName = cleanVarNameBuilder.toString();
        cleanVarNameBuilder.setLength(0);
        return varName;
    }

    public String formatExprAsSimple(String expr, Map<String, BigDecimal> varsMap) {
        return formatExprAsSimple(expr, 0, varsMap);
    }

    public String formatExprAsSimple(String expr, int depth, Map<String, BigDecimal> varsMap) {
        LinkedList<StringBuilder> nestExprBuilders = new LinkedList<>();
        List<String> simpleNestExprList = new ArrayList<>();
        for (char c : expr.toCharArray()) {
            for (StringBuilder builder : nestExprBuilders) {
                builder.append(c);
            }
            if (c == '(') {
                nestExprBuilders.addLast(new StringBuilder().append(c));
            }
            if (c == ')') {
                if (nestExprBuilders.isEmpty()) {
                    throw new CalculateException(String.format("Invalid expression: %s", Context.originalExpr()));
                }
                StringBuilder nestExprBuilder = nestExprBuilders.getLast();
                String nestExpr = nestExprBuilder.toString();
                if (!nestExpr.substring(1).contains("(") && !simpleNestExprList.contains(nestExpr)) {
                    simpleNestExprList.add(nestExpr);
                }
                nestExprBuilder.setLength(0);
                nestExprBuilders.removeLast();
            }
        }
        if (!nestExprBuilders.isEmpty()) {
            throw new CalculateException(String.format("Invalid expression: %s", Context.originalExpr()));
        }
        if (simpleNestExprList.isEmpty()) {
            return expr;
        }
        StringBuilder varNameBuilder = newStrBuilder();
        String simpleNestExpr;
        String newVarName;
        for (int i = 0; i < simpleNestExprList.size(); i++) {
            varNameBuilder.setLength(0);
            newVarName = varNameBuilder
                    .append(TEMP_VAR_NAME_OPEN)
                    .append(depth).append(TEMP_VAR_NAME_DELIMITER).append(i)
                    .append(TEMP_VAR_NAME_CLOSE)
                    .toString();
            varNameBuilder.setLength(0);
            simpleNestExpr = simpleNestExprList.get(i);
            varsMap.put(newVarName, calculateSimpleExpr(simpleNestExpr, varsMap));
            expr = expr.replaceAll(exprToRegex(simpleNestExpr), newVarName);
        }
        varNameBuilder.setLength(0);
        if (expr.contains("(")) {
            depth++;
            return formatExprAsSimple(expr, depth, varsMap);
        }
        return expr;
    }

    protected BigDecimal calculateSimpleExpr(String expr, Map<String, BigDecimal> varsMap) {
        int openIndex = expr.lastIndexOf("(");
        int closeIndex = expr.indexOf(")");
        if (openIndex > 0 || (closeIndex >= 0 && closeIndex != expr.length() - 1)) {
            throw new CalculateException("Unable to support nested calculations");
        }
        if (openIndex == 0) {
            expr = expr.substring(1);
        }
        if (closeIndex >= 0) {
            expr = expr.substring(0, expr.length() - 1);
        }
        Token root = new Token(BigDecimal.ZERO, SimpleOpt.add);
        Token advancedTokenGroup = null;
        StringBuilder tokenNameBuilder = newStrBuilder();
        String tokenName;
        BigDecimal tokenValue;
        Opt opt;
        char[] chars = expr.toCharArray();
        if (supportedOpts.containsKey(chars[0]) || supportedOpts.containsKey(chars[chars.length - 1])) {
            throw new CalculateException(String.format("Invalid expression: %s", Context.originalExpr()));
        }
        char c;
        for (int i = 0; i < chars.length; i++) {
            c = chars[i];
            opt = supportedOpts.get(c);
            if (opt == null) {
                tokenNameBuilder.append(c);
                if (i < chars.length - 1) {
                    continue;
                }
            }
            tokenName = tokenNameBuilder.toString();
            tokenNameBuilder.setLength(0);
            if (tokenName.length() == 0) {
                throw new CalculateException(String.format("Invalid expression: %s", Context.originalExpr()));
            }
            try {
                Double.parseDouble(tokenName);
                tokenValue = new BigDecimal(tokenName);
            } catch (Exception e) {
                tokenValue = varsMap.get(tokenName);
                if (tokenValue == null) {
                    throw new CalculateException(String.format("Missing value for variable '%s'", tokenName));
                }
            }
            Token nestFormulaToken = new Token(tokenValue, opt);
            if (opt != null && opt.isAdvanced()) {
                advancedTokenGroup = advancedTokenGroup == null ?
                        new Token(BigDecimal.ONE, SimpleOpt.multiply) : advancedTokenGroup;
                advancedTokenGroup.append(nestFormulaToken);
            } else {
                if (advancedTokenGroup != null) {
                    nestFormulaToken.setOpt(null);
                    advancedTokenGroup.append(nestFormulaToken);
                    nestFormulaToken = new Token(advancedTokenGroup.val(), opt);
                    root.append(nestFormulaToken);
                    advancedTokenGroup = null;
                } else {
                    root.append(nestFormulaToken);
                }
            }
        }
        tokenNameBuilder.setLength(0);
        return root.val();
    }

    protected boolean isIgnoreChar(char c) {
        return c == '\r' || c == '\n' || Character.isSpaceChar(c);
    }

    protected boolean isNormalChar(char c) {
        return Character.isDigit(c) || Character.isLetter(c) || (c >= 0x4E00 && c <= 0x9FA5);
    }

    protected char exchangeExprChar(char c) {
        switch (c) {
            case '（':
                return '(';
            case '）':
                return ')';
            default:
                Opt opt = supportedOpts.get(c);
                if (opt != null) {
                    return opt.getSymbol();
                }
                return c;
        }
    }

    protected String exprToRegex(String expr) {
        StringBuilder builder = newStrBuilder();
        Opt opt;
        for (char c : expr.toCharArray()) {
            switch (c) {
                case '(':
                    builder.append("\\(");
                    break;
                case ')':
                    builder.append("\\)");
                    break;
                case '.':
                    builder.append("\\.");
                    break;
                default:
                    opt = supportedOpts.get(c);
                    if (opt != null) {
                        if (opt.getPattern() == null || opt.getPattern().pattern() == null) {
                            throw new CalculateException(String.format("The regex pattern of character '%s' is not defined", c));
                        }
                        builder.append(opt.getPattern().pattern());
                    } else {
                        builder.append(c);
                    }
                    break;
            }
        }
        String regex = builder.toString();
        builder.setLength(0);
        return regex;
    }

    protected boolean isValidExprChar(char c) {
        if (c == '(' || c == ')' || c == '.' || isNormalChar(c)) {
            return true;
        }
        return supportedOpts.get(c) != null;
    }

    protected boolean isValidVarNameChar(char c) {
        return isNormalChar(c);
    }

    public static class Token {
        @Getter
        @Setter
        private BigDecimal value;
        @Getter
        @Setter
        private Opt opt;
        private Token next;
        private Token last;

        public Token(BigDecimal value, Opt opt) {
            this.value = value;
            this.opt = opt;
        }

        public void append(Token token) {
            if (next == null) {
                next = token;
            }
            if (last != null) {
                last.next = token;
            }
            last = token;
        }

        public BigDecimal val() {
            BigDecimal value = this.value;
            Opt opt = this.opt;
            Token nextToken = next;
            while (nextToken != null) {
                try {
                    value = opt.getFunc().apply(value, nextToken.getValue());
                } catch (ArithmeticException ae) {
                    throw new CalculateException("Expression not compliant", ae);
                }
                opt = nextToken.opt;
                nextToken = nextToken.next;
            }
            return value;
        }
    }

    public interface Opt {

        char getSymbol();

        char getCnSymbol();

        Pattern getPattern();

        boolean isAdvanced();

        BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunc();
    }

    @Getter
    public enum SimpleOpt implements Opt {
        add('+', Pattern.compile("\\+"), BigDecimal::add),
        subtract('-', Pattern.compile("-"), BigDecimal::subtract),
        multiply('*', '×', Pattern.compile("\\*"), true, BigDecimal::multiply),
        divide('/', Pattern.compile("/"), true, (l, r) -> l.divide(r, Context.calculateScale(), Context.roundingMode())),
        ;

        private final char symbol;
        private final char cnSymbol;
        private final Pattern pattern;
        private final boolean advanced;
        private final BiFunction<BigDecimal, BigDecimal, BigDecimal> func;

        SimpleOpt(char symbol, Pattern pattern, BiFunction<BigDecimal, BigDecimal, BigDecimal> func) {
            this(symbol, pattern, false, func);
        }

        SimpleOpt(char symbol, Pattern pattern, boolean advanced, BiFunction<BigDecimal, BigDecimal, BigDecimal> func) {
            this(symbol, symbol, pattern, advanced, func);
        }

        SimpleOpt(char symbol, char cnSymbol, Pattern pattern, boolean advanced, BiFunction<BigDecimal, BigDecimal, BigDecimal> func) {
            this.symbol = symbol;
            this.cnSymbol = cnSymbol;
            this.pattern = pattern;
            this.advanced = advanced;
            this.func = func;
        }

        public static SimpleOpt of(char c) {
            for (SimpleOpt opt : values()) {
                if (opt.symbol == c) {
                    return opt;
                }
            }
            return null;
        }
    }

    @Getter
    public static class Context {
        private static final ThreadLocal<Context> HOLDER = new ThreadLocal<>();
        private static final ThreadLocal<StringBuilder> STR_BUILDER = new ThreadLocal<>();
        private final String originalExpr;
        private final String expression;
        private final Map<String, BigDecimal> variables;
        private final int calculateScale;
        private final RoundingMode roundingMode;
        private final Map<Character, Opt> supportedOpts;

        private Context(String originalExpr, String expression, Map<String, BigDecimal> variables, int calculateScale,
                        RoundingMode roundingMode, Map<Character, Opt> supportedOpts) {
            this.originalExpr = originalExpr;
            this.expression = expression;
            this.variables = variables;
            this.calculateScale = calculateScale;
            this.roundingMode = roundingMode;
            this.supportedOpts = supportedOpts;
        }

        public static Context init(String originalExpr, String expression, Map<String, BigDecimal> variables, int calculateScale,
                                   RoundingMode roundingMode, Map<Character, Opt> supportedOpts) {
            Context context = new Context(originalExpr, expression, variables, calculateScale, roundingMode, supportedOpts);
            if (now(false) != null) {
                throw new CalculateException("The context has already been initialized");
            }
            HOLDER.set(context);
            return context;
        }

        public static void destroy() {
            Context context = now(false);
            if (context != null) {
                context.variables.clear();
            }
            HOLDER.remove();
            StringBuilder strBuilder = STR_BUILDER.get();
            if (strBuilder != null) {
                strBuilder.setLength(0);
            }
            STR_BUILDER.remove();
        }

        public static Context now() {
            return now(true);
        }

        public static Context now(boolean check) {
            if (check) {
                if (HOLDER.get() == null) {
                    throw new CalculateException("The context has not been initialized yet");
                }
            }
            return HOLDER.get();
        }

        public static String originalExpr() {
            return now().originalExpr;
        }

        public static String expression() {
            return now().expression;
        }

        public static int calculateScale() {
            return now().calculateScale;
        }

        public static RoundingMode roundingMode() {
            return now().roundingMode;
        }

        public static StringBuilder newStrBuilder() {
            StringBuilder strBuilder = STR_BUILDER.get();
            if (strBuilder == null) {
                strBuilder = new StringBuilder();
                STR_BUILDER.set(strBuilder);
                return strBuilder;
            }
            if (strBuilder.length() == 0) {
                return strBuilder;
            }
            log.warn("Suggest trying to reset the global string builder to reuse it");
            return new StringBuilder();
        }
    }

    public static class CalculateException extends RuntimeException {
        private static final long serialVersionUID = 6575597975452654612L;

        public CalculateException(String message) {
            super(message);
        }

        public CalculateException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
