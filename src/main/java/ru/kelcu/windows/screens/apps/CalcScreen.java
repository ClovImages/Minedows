package ru.kelcu.windows.screens.apps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringUtil;
import org.lwjgl.glfw.GLFW;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;

public class CalcScreen extends Screen {
    public CalcScreen() {
        super(Component.translatable("minedows.calc"));
    }

    @Override
    protected void init() {
        super.init();
        int w = width/4;
        int h = (height-35)/4;
        int x = 0; int y = 35;
        addRenderableWidget(new ButtonBuilder(Component.literal("7"), (s) -> charTyped('7', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("8"), (s) -> charTyped('8', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("9"), (s) -> charTyped('9', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("."), (s) -> charTyped('.', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x=0;y+=h;
        addRenderableWidget(new ButtonBuilder(Component.literal("4"), (s) -> charTyped('4', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("5"), (s) -> charTyped('5', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("6"), (s) -> charTyped('6', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("/"), (s) -> charTyped('/', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x=0;y+=h;
        addRenderableWidget(new ButtonBuilder(Component.literal("1"), (s) -> charTyped('1', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("2"), (s) -> charTyped('2', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("3"), (s) -> charTyped('3', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("*"), (s) -> charTyped('*', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x=0;y+=h;
        addRenderableWidget(new ButtonBuilder(Component.literal("-"), (s) -> charTyped('-', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("0"), (s) -> charTyped('0', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("+"), (s) -> charTyped('+', 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x+=w;
        addRenderableWidget(new ButtonBuilder(Component.literal("="), (s) -> keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0)).setPosition(x+1, y+1).setSize(w-2, h-2).setStyle(Windows.minedowsStyle).build());
        x=0;y+=h;
    }

    String matematika_blyadskaya = "";

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, 0, width, 30);
        int w = font.width(matematika_blyadskaya);
        guiGraphics.drawString(font, matematika_blyadskaya, width-5-w, 15-(font.lineHeight/2), 0xFF000000, false);
    }

    boolean isError = false;
    @Override
    public boolean charTyped(char c, int i) {
        if (StringUtil.isAllowedChatCharacter(c) && correctForMath(Character.toString(c))) {
            if(isError) {
                isError = false;
                matematika_blyadskaya = "";
            }
            matematika_blyadskaya+= Character.toString(c);
            return true;
        } else {
            SoundUtils.error();
            return false;
        }
    }
    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_ENTER && !matematika_blyadskaya.isBlank()){
            try {
                matematika_blyadskaya = String.valueOf(eval(matematika_blyadskaya));
                if(matematika_blyadskaya.endsWith(".0"))matematika_blyadskaya = matematika_blyadskaya.replace(".0", "");
            } catch (Exception ex){
                isError = true;
                matematika_blyadskaya = "Error";
                ex.printStackTrace();
            }
            return true;
        }
        else if(i == GLFW.GLFW_KEY_BACKSPACE && !matematika_blyadskaya.isBlank()){
            matematika_blyadskaya = matematika_blyadskaya.substring(0, matematika_blyadskaya.length()-1);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    public static boolean correctForMath(String character){
        return character.matches("[0-9-+/*E.]");
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
