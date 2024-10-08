package Conference.video.Controller;
import org.springframework.messaging.handler.annotation.SendTo;
@Controller
public class SignalingController {

    @MessageMapping("/video-offer")
    @SendTo("/topic/video-offer")
    public String handleOffer(String offer) {
        return offer;  
    }
}
