package com.email.verification.email.mapper;

import com.email.verification.email.dto.EmailInfoDto;
import com.email.verification.email.model.EmailInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EmailInfoMapper {
    EmailInfoDto toDto(EmailInfo entity);
}
