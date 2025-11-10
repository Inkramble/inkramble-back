package inkramble.client;

import java.util.UUID;

public record ClientSubscribeRequest(
        UUID id,
        String rootPath
) {
}
