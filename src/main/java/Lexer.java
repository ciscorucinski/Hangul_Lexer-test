import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.UnicodeBlock;

public class Lexer {

    public enum LanguageBlock {
        ENGLISH (UnicodeBlock.BASIC_LATIN, UnicodeBlock.LATIN_1_SUPPLEMENT, UnicodeBlock.LATIN_EXTENDED_A, UnicodeBlock.GENERAL_PUNCTUATION),
        KOREAN (UnicodeBlock.HANGUL_JAMO);

        List<UnicodeBlock> language = new ArrayList<>();

        LanguageBlock(UnicodeBlock ... unicodeBlocks) {
            language.addAll(Arrays.asList(unicodeBlocks));
        }

    }
    public enum TokenType {
        // Token types cannot have underscores
        HANGUL ("[가-힣]"),
        LETTER("[a-z]|[A-Z]"),

        NUM ("-?[0-9]"),

        PUNCT ("[!|?|.]"),
        WS ("[ \t\f\r\n]");

        private final String pattern;
        private static Pattern compiledPattern;

        static {
            StringBuilder regexPattern = new StringBuilder();
            for (TokenType token: TokenType.values()) {
                if (token.pattern.equals("")) continue;
                regexPattern.append(String.format("|(?<%s>%s)", token.name(), token.pattern));
            }
            regexPattern.deleteCharAt(0);
            compiledPattern = Pattern.compile(regexPattern.toString());
        }

        TokenType(String pattern) {
            this.pattern = pattern;
        }

        public static Pattern getTokenRegexPattern() {
            return compiledPattern;
        }

        public Token is(Matcher matcher) {
            return new Token(this, matcher.group(this.name()));
        }
    }

    public static class Token {
        TokenType type;
        String data;

        Token(TokenType type, String data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("%s,\t\t%s", type.name(), data);
        }
    }

    private static ArrayList<Token> lex(String input) {
        // The tokens to return
        ArrayList<Token> tokens = new ArrayList<>();

        Pattern tokenPatterns = TokenType.getTokenRegexPattern();

        for (int index = 0; index < input.length(); index++) {
            CharSequence character = input.subSequence(index, index + 1);
            System.out.println(character);
            Matcher matched = tokenPatterns.matcher(character);

            while (matched.find()) {
                if (matched.group(TokenType.NUM.name()) != null) {
                    tokens.add(TokenType.NUM.is(matched));
                    continue;
                } else if (matched.group(TokenType.PUNCT.name()) != null) {
                    tokens.add(new Token(TokenType.PUNCT, matched.group(TokenType.PUNCT.name())));
                    continue;
                } else if (matched.group(TokenType.WS.name()) != null) {
                    tokens.add(new Token(TokenType.WS, matched.group(TokenType.WS.name())));
                    continue;
                } else if (matched.group(TokenType.LETTER.name()) != null) {
                    tokens.add(new Token(TokenType.LETTER, matched.group(TokenType.LETTER.name())));
                    continue;
                } else if (matched.group(TokenType.HANGUL.name()) != null) {
                    tokens.add(new Token(TokenType.HANGUL, matched.group(TokenType.HANGUL.name())));
                    continue;
                }

            }
        }

        return tokens;
    }

    public static void main(String[] args) {
//        String input = "Hello World! My name is Christopher Rucinski. I am 32 years old! 한글.";
        String input = "저의 어머니는 올해 한국에 올 것입니다";

        // Create tokens and print them
        ArrayList<Token> tokens = lex(input);
        for (Token token : tokens)
            System.out.println(token);
    }
}