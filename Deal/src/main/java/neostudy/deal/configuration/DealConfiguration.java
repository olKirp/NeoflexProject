package neostudy.deal.configuration;

import neostudy.deal.mapper.ClientMapper;
import neostudy.deal.mapper.ScoringDataDTOMapper;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DealConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ClientMapper clientMapper() {
        return Mappers.getMapper(ClientMapper.class);
    }

    @Bean
    public ScoringDataDTOMapper scoringDataDTOMapper() {
        return Mappers.getMapper(ScoringDataDTOMapper.class);
    }
}
