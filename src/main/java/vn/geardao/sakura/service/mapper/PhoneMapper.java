package vn.geardao.sakura.service.mapper;

import vn.geardao.sakura.domain.*;
import vn.geardao.sakura.service.dto.PhoneDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Phone and its DTO PhoneDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PhoneMapper extends EntityMapper<PhoneDTO, Phone> {



    default Phone fromId(Long id) {
        if (id == null) {
            return null;
        }
        Phone phone = new Phone();
        phone.setId(id);
        return phone;
    }


}
