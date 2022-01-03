package com.acme.workorderapi.domain;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ClassificationsConverter implements AttributeConverter<ClassificationType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(ClassificationType classification) {
		if(classification == null) {
			return null;
		}
		return classification.getValue();
	}

	@Override
	public ClassificationType convertToEntityAttribute(Integer code) {
		if (code == null) {
            return null;
        }
		
		return Stream.of(ClassificationType.values())
		          .filter(c -> c.getValue().equals(code))
		          .findFirst()
		          .orElseThrow(IllegalArgumentException::new);
	}	
}
