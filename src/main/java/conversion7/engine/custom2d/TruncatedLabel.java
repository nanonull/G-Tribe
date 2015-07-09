package conversion7.engine.custom2d;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import conversion7.game.ui.HintForm;

public class TruncatedLabel extends Label {

    private int truncateLimit;
    private String originalText;
    private boolean truncated;
    private boolean assignHintIfTruncated = true;
    private boolean truncating;

    public TruncatedLabel(int truncateLimit, CharSequence text, LabelStyle style) {
        super(text, style);
        this.truncateLimit = truncateLimit;
        this.originalText = String.valueOf(text);
        trimText();
    }

    public void setAssignHintIfTruncated(boolean assignHintIfTruncated) {
        this.assignHintIfTruncated = assignHintIfTruncated;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncateLimit(int truncateLimit) {
        this.truncateLimit = truncateLimit;
    }

    public int getTruncateLimit() {
        return truncateLimit;
    }

    @Override
    public void setText(CharSequence newText) {
        super.setText(newText);
        if (!truncating) {
            originalText = String.valueOf(newText);
        }
    }

    @Override
    public void layout() {
        trimText();
        super.layout();
    }

    private void trimText() {
        truncating = true;
        truncated = false;
        GlyphLayout glyphLayout = getGlyphLayout();
        String textWip = getText().toString();
        String textWipWithDots = null;
        while (glyphLayout.width > truncateLimit) {
            textWip = textWip.substring(0, textWip.length() - 1);
            textWipWithDots = textWip + "..";
            glyphLayout.setText(getBitmapFontCache().getFont(), textWipWithDots);
        }

        if (textWipWithDots != null) {
            truncated = true;
            setText(textWipWithDots);
            if (assignHintIfTruncated) {
                HintForm.assignHintTo(this, originalText);
            }
        }
        truncating = false;
    }
}
