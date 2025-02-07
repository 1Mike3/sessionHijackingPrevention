package proj.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class FingerprintData {
    private Integer blockId;
    private String IP;
    //TODO: Change to Location Class
    //TODO: Introduce UserAgent Parser
    private float longitude;
    private float latitude;
    private boolean cookiesAccepted;
    private String userAgent;
    private String content_Language;
    private String timezone;
    private String screen;

//For Debug prints
    @Override
    public String toString(){
        return String.format("""
                blockId: %s
                IP: %s
                userAgent: %s
                content_Language: %s
                timezone: %s
                screen: %s
                """,blockId,IP,userAgent,content_Language,timezone,screen);
    }
}
