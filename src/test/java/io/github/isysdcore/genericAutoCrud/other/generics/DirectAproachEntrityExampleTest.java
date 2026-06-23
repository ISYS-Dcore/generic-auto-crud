package io.github.isysdcore.genericAutoCrud.other.generics;

import io.github.isysdcore.genericAutoCrud.generics.RestIntegrationTestsImpl;
import io.github.isysdcore.genericAutoCrud.other.EntityExample;
import io.github.isysdcore.genericAutoCrud.other.EntityExampleDto;

public class DirectAproachEntrityExampleTest extends RestIntegrationTestsImpl<EntityExample, String> {
    public DirectAproachEntrityExampleTest() {
        super("/vn/api/directEntity", new EntityExample());
    }

    // Optional: pre‑populate the DB using the real service
    @Override
    protected void storeEntitiesInDb(int quantity) {
        // subscriberService.saveAll(...)
    }
}