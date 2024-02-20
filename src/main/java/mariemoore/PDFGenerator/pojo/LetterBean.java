package mariemoore.PDFGenerator.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LetterBean {
    private boolean showLogoAndFooter;
    private boolean isAddressStamped;
    private boolean isPaginationStamped;
    private boolean imgOddPage;
    private String date;
    private String prefix;
    private String name;
    private Collection<String> adress;

}
