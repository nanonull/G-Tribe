
package conversion7.engine.dialog.convertor.model.drawio;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class Root {

    @SerializedName("mxCell")
    @Expose
    public List<DrawioCell> drawioCells = new ArrayList<DrawioCell>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
