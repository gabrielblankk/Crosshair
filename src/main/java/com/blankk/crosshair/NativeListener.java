package com.blankk.crosshair;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

public abstract class NativeListener {
    private static NativeKeyListener keyListener;
    private static NativeMouseListener mouseListener;

    public static void addBindListeners(BindConsumer onBind) {
        keyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                onBind.consume(NativeKeyEvent.getKeyText(e.getKeyCode()), () -> removeListeners());
            }
        };

        mouseListener = new NativeMouseListener() {
            @Override
            public void nativeMousePressed(NativeMouseEvent e) {
                String mouseButtonString = switch (e.getButton()) {
                    case 1 -> "Left";
                    case 2 -> "Right";
                    case 3 -> "Scroll";
                    case 4 -> "Thumb Down";
                    case 5 -> "Thumb Up";
                    default -> "";
                };

                onBind.consume(mouseButtonString, () -> removeListeners());
            }
        };

        GlobalScreen.addNativeKeyListener(keyListener);
        GlobalScreen.addNativeMouseListener(mouseListener);
    }

    private static void removeListeners() {
        GlobalScreen.removeNativeKeyListener(keyListener);
        GlobalScreen.removeNativeMouseListener(mouseListener);
    }

    @FunctionalInterface
    public interface BindConsumer {
        void consume(String bind, Runnable removeListeners);
    }
}
