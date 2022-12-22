package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.callables.EventCancellable;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import net.minecraft.entity.EntityLivingBase;

/**
* post can't be cancelled
*/
public class RenderAliveMobEvent<T extends EntityLivingBase> extends EventCancellable {
    private final EventType eventType;
    private EntityLivingBase entity;
    public final double x;
    public final double y;
    public final double z;

    public RenderAliveMobEvent(EventType eventType, EntityLivingBase entity, double x, double y, double z) {
        this.eventType = eventType;
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}
