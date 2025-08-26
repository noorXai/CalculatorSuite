import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;

public class CalculatorSuite extends JFrame {
    private final JTextField display = new JTextField();
    private String currentInput = "";
    private double firstNumber = 0;
    private String operator = "";

    private final Color bgStart = new Color(18, 18, 25);
    private final Color bgEnd = new Color(35, 35, 55);
    private final Color panelBg = new Color(30, 30, 45);
    private final Color accent = new Color(255, 105, 180);
    private final Font displayFont = new Font("Segoe UI", Font.BOLD, 32);
    private final Font heartFont = new Font("Segoe UI", Font.BOLD, 20);

    public CalculatorSuite() {
        setTitle("CalculatorSuite — by noorXai");
        setSize(420, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        // Gradient background
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, bgStart, 0, getHeight(), bgEnd);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        content.setLayout(null);
        setContentPane(content);

        setupDisplay(content);
        setupHeartButtons(content);

        setVisible(true);
    }

    private void setupDisplay(JPanel parent) {
        display.setBounds(30, 30, 360, 70);
        display.setFont(displayFont);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(panelBg);
        display.setForeground(accent);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 160), 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        display.setText("0");
        parent.add(display);
    }

    private void setupHeartButtons(JPanel parent) {
        String[] labels = {
                "AC", "⌫", "/", "*",
                "7", "8", "9", "-",
                "4", "5", "6", "+",
                "1", "2", "3", "=",
                "0", ".", "", ""
        };

        int x = 30, y = 120;
        int w = 80, h = 70;
        int idx = 0;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                if (idx >= labels.length) break;
                String label = labels[idx++];
                if (label.isEmpty()) {
                    x += 90;
                    continue;
                }

                HeartButton btn = new HeartButton(label);
                btn.setFont(heartFont);
                btn.setForeground(Color.WHITE);
                btn.setBounds(x, y, w, h);
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setAccentColor(accent);
                btn.addActionListener(e -> handleButton(label));
                parent.add(btn);

                x += 90;
            }
            x = 30;
            y += 85;
        }
    }

    private void handleButton(String value) {
        switch (value) {
            case "AC":
                currentInput = "";
                firstNumber = 0;
                operator = "";
                display.setText("0");
                break;
            case "⌫":
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(currentInput.isEmpty() ? "0" : currentInput);
                }
                break;
            case "+": case "-": case "*": case "/":
                try {
                    firstNumber = Double.parseDouble(currentInput.isEmpty() ? "0" : currentInput);
                    operator = value;
                    currentInput = "";
                    display.setText("");
                } catch (Exception e) {
                    display.setText("Error");
                    display.setForeground(Color.ORANGE);
                }
                break;
            case "=":
                try {
                    double secondNumber = Double.parseDouble(currentInput.isEmpty() ? "0" : currentInput);
                    double result = 0;

                    switch (operator) {
                        case "+": result = firstNumber + secondNumber; break;
                        case "-": result = firstNumber - secondNumber; break;
                        case "*": result = firstNumber * secondNumber; break;
                        case "/":
                            if (secondNumber == 0) {
                                display.setText("Cannot ÷ 0");
                                display.setForeground(Color.RED);
                                return;
                            }
                            result = firstNumber / secondNumber;
                            break;
                        default:
                            result = secondNumber;
                    }

                    String out = formatResult(result);
                    display.setText(out);
                    currentInput = out;
                    operator = "";
                    display.setForeground(accent);
                } catch (Exception e) {
                    display.setText("Error");
                    display.setForeground(Color.ORANGE);
                }
                break;
            default:
                if (currentInput.equals("0")) currentInput = "";
                currentInput += value;
                display.setText(currentInput);
        }
    }

    private String formatResult(double val) {
        if (Math.abs(val - Math.round(val)) < 1e-12) {
            return String.format("%d", Math.round(val));
        } else {
            return String.format("%s", val);
        }
    }

    // Custom heart-shaped button
    static class HeartButton extends JButton {
        private Color accent = new Color(255, 105, 180);
        private final Color base = new Color(60, 60, 90);
        private boolean hover = false;
        private boolean pressed = false;

        public HeartButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setFocusPainted(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setAccentColor(Color c) {
            this.accent = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // background heart shape
            Shape heart = createHeartShape(w, h);
            Color fill = base;
            if (pressed) fill = fill.darker();
            else if (hover) fill = fill.brighter();
            g2.setPaint(new GradientPaint(0, 0, fill, w, h, accent, true));
            g2.fill(heart);

            // subtle glow border
            g2.setStroke(new BasicStroke(2));
            g2.setColor(accent);
            g2.draw(heart);

            // draw the text centered
            FontMetrics fm = g2.getFontMetrics(getFont());
            String text = getText();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int tx = (w - textWidth) / 2;
            int ty = (h + textHeight) / 2 - 4;
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            g2.drawString(text, tx, ty);

            g2.dispose();
        }

        private Shape createHeartShape(int w, int h) {
            double scale = Math.min(w, h) / 100.0;
            GeneralPath path = new GeneralPath();
            path.moveTo(50 * scale, 30 * scale);
            path.curveTo(35 * scale, 0 * scale, 0 * scale, 25 * scale, 50 * scale, 70 * scale);
            path.curveTo(100 * scale, 25 * scale, 65 * scale, 0 * scale, 50 * scale, 30 * scale);
            return path;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorSuite::new);
    }
}
