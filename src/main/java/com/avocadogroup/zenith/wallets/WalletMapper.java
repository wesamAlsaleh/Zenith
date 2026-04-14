package com.avocadogroup.zenith.wallets;

import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting between {@link Wallet} entities and {@link WalletDto} objects.
 * <p>
 * Field mapping is automatic since the entity and DTO share identical field names and compatible types.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletDto toDto(Wallet wallet);
}
