package CS203G3.tariff_backend.dto;

public class TariffUpdateDto {
    private Long tariffID;
    private TariffCreateDto tariffCreateDto;

    public Long getTariffID() {
        return tariffID;
    }

    public void setTariffID(Long tariffID) {
        this.tariffID = tariffID;
    }

    public TariffCreateDto getTariffCreateDto() {
        return tariffCreateDto;
    }

    public void setTariffCreateDto(TariffCreateDto tariffCreateDto) {
        this.tariffCreateDto = tariffCreateDto;
    }
}
