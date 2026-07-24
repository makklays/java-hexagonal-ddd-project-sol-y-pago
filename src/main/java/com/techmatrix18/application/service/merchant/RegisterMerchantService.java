package com.techmatrix18.application.service.merchant;

import com.techmatrix18.application.command.merchant.RegisterMerchantCommand;
import com.techmatrix18.application.port.in.merchant.RegisterMerchantUseCase;
import com.techmatrix18.application.port.out.merchant.MerchantRepositoryPort;
import com.techmatrix18.domain.merchant.Merchant;

/**
 * Service class for registering a new merchant. This class implements the RegisterMerchantUseCase interface and
 * handles the business logic for creating and persisting a new merchant entity.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 24.07.2026
 */
public class RegisterMerchantService implements RegisterMerchantUseCase {

    private final MerchantRepositoryPort merchantRepositoryPort;

    public RegisterMerchantService(MerchantRepositoryPort merchantRepositoryPort) {
        this.merchantRepositoryPort = merchantRepositoryPort;
    }

    @Override
    public Merchant register(RegisterMerchantCommand command) {
        // Создаем чистый доменный объект Мерчанта (внутри фабрики сработает валидация)
        Merchant merchant = Merchant.createNew(command.name(), command.rawApiKey());

        // Сохраняем в базу данных через исходящий порт
        return merchantRepositoryPort.save(merchant);
    }
}

