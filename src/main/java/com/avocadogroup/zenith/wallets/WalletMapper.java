package com.avocadogroup.zenith.wallets;

import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletDto toDto(Wallet wallet);
}
