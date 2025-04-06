package net.lax1dude.eaglercraft.backend.plan;

import com.djrapitops.plan.capability.CapabilityService;
import com.djrapitops.plan.extension.ExtensionService;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public class PlanHook {
    public static void hookIntoPlan(IEaglerXServerAPI<?> serverAPI) {
        if (!CapabilityService.getInstance().hasCapability("DATA_EXTENSION_VALUES")) return;
        ExtensionService.getInstance().register(new EaglerXDataExtension(serverAPI));
        CapabilityService.getInstance().registerEnableListener(isPlanEnabled -> {
            if (isPlanEnabled) {
                ExtensionService.getInstance().register(new EaglerXDataExtension(serverAPI));
            }
        });
    }
}
