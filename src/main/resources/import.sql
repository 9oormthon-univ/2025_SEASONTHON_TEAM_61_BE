-- src/main/resources/data.sql
-- 대한민국 전체 광역자치단체 및 시군구 지역 코드/이름 데이터

-- 1. 광역자치단체 (Provinces / Metropolitan Cities)
INSERT INTO REGION (region_code, region_name) VALUES ('11000', '서울특별시');
INSERT INTO REGION (region_code, region_name) VALUES ('26000', '부산광역시');
INSERT INTO REGION (region_code, region_name) VALUES ('27000', '대구광역시');
INSERT INTO REGION (region_code, region_name) VALUES ('28000', '인천광역시');
INSERT INTO REGION (region_code, region_name) VALUES ('29000', '광주광역시');
INSERT INTO REGION (region_code, region_name) VALUES ('30000', '대전광역시');
INSERT INTO REGION (region_code, region_name) VALUES ('31000', '울산광역시');
INSERT INTO REGION (region_code, region_name) VALUES ('36110', '세종특별자치시');
INSERT INTO REGION (region_code, region_name) VALUES ('41000', '경기도');
INSERT INTO REGION (region_code, region_name) VALUES ('42000', '강원특별자치도');
INSERT INTO REGION (region_code, region_name) VALUES ('43000', '충청북도');
INSERT INTO REGION (region_code, region_name) VALUES ('44000', '충청남도');
INSERT INTO REGION (region_code, region_name) VALUES ('45000', '전북특별자치도');
INSERT INTO REGION (region_code, region_name) VALUES ('46000', '전라남도');
INSERT INTO REGION (region_code, region_name) VALUES ('47000', '경상북도');
INSERT INTO REGION (region_code, region_name) VALUES ('48000', '경상남도');
INSERT INTO REGION (region_code, region_name) VALUES ('50000', '제주특별자치도');

-- 2. 서울특별시 - 시군구 (Districts of Seoul)
INSERT INTO REGION (region_code, region_name) VALUES ('11110', '서울특별시 종로구');
INSERT INTO REGION (region_code, region_name) VALUES ('11140', '서울특별시 중구');
INSERT INTO REGION (region_code, region_name) VALUES ('11170', '서울특별시 용산구');
INSERT INTO REGION (region_code, region_name) VALUES ('11200', '서울특별시 성동구');
INSERT INTO REGION (region_code, region_name) VALUES ('11215', '서울특별시 광진구');
INSERT INTO REGION (region_code, region_name) VALUES ('11230', '서울특별시 동대문구');
INSERT INTO REGION (region_code, region_name) VALUES ('11260', '서울특별시 중랑구');
INSERT INTO REGION (region_code, region_name) VALUES ('11290', '서울특별시 성북구');
INSERT INTO REGION (region_code, region_name) VALUES ('11305', '서울특별시 강북구');
INSERT INTO REGION (region_code, region_name) VALUES ('11320', '서울특별시 도봉구');
INSERT INTO REGION (region_code, region_name) VALUES ('11350', '서울특별시 노원구');
INSERT INTO REGION (region_code, region_name) VALUES ('11380', '서울특별시 은평구');
INSERT INTO REGION (region_code, region_name) VALUES ('11410', '서울특별시 서대문구');
INSERT INTO REGION (region_code, region_name) VALUES ('11440', '서울특별시 마포구');
INSERT INTO REGION (region_code, region_name) VALUES ('11470', '서울특별시 양천구');
INSERT INTO REGION (region_code, region_name) VALUES ('11500', '서울특별시 강서구');
INSERT INTO REGION (region_code, region_name) VALUES ('11530', '서울특별시 구로구');
INSERT INTO REGION (region_code, region_name) VALUES ('11545', '서울특별시 금천구');
INSERT INTO REGION (region_code, region_name) VALUES ('11560', '서울특별시 영등포구');
INSERT INTO REGION (region_code, region_name) VALUES ('11590', '서울특별시 동작구');
INSERT INTO REGION (region_code, region_name) VALUES ('11620', '서울특별시 관악구');
INSERT INTO REGION (region_code, region_name) VALUES ('11650', '서울특별시 서초구');
INSERT INTO REGION (region_code, region_name) VALUES ('11680', '서울특별시 강남구');
INSERT INTO REGION (region_code, region_name) VALUES ('11710', '서울특별시 송파구');
INSERT INTO REGION (region_code, region_name) VALUES ('11740', '서울특별시 강동구');

-- 3. 부산광역시 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('26110', '부산광역시 중구');
INSERT INTO REGION (region_code, region_name) VALUES ('26140', '부산광역시 서구');
INSERT INTO REGION (region_code, region_name) VALUES ('26170', '부산광역시 동구');
INSERT INTO REGION (region_code, region_name) VALUES ('26200', '부산광역시 영도구');
INSERT INTO REGION (region_code, region_name) VALUES ('26230', '부산광역시 부산진구');
INSERT INTO REGION (region_code, region_name) VALUES ('26260', '부산광역시 동래구');
INSERT INTO REGION (region_code, region_name) VALUES ('26290', '부산광역시 남구');
INSERT INTO REGION (region_code, region_name) VALUES ('26320', '부산광역시 북구');
INSERT INTO REGION (region_code, region_name) VALUES ('26350', '부산광역시 해운대구');
INSERT INTO REGION (region_code, region_name) VALUES ('26380', '부산광역시 사하구');
INSERT INTO REGION (region_code, region_name) VALUES ('26410', '부산광역시 금정구');
INSERT INTO REGION (region_code, region_name) VALUES ('26440', '부산광역시 강서구');
INSERT INTO REGION (region_code, region_name) VALUES ('26470', '부산광역시 연제구');
INSERT INTO REGION (region_code, region_name) VALUES ('26500', '부산광역시 수영구');
INSERT INTO REGION (region_code, region_name) VALUES ('26530', '부산광역시 사상구');
INSERT INTO REGION (region_code, region_name) VALUES ('26710', '부산광역시 기장군');

-- 4. 대구광역시 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('27110', '대구광역시 중구');
INSERT INTO REGION (region_code, region_name) VALUES ('27140', '대구광역시 동구');
INSERT INTO REGION (region_code, region_name) VALUES ('27170', '대구광역시 서구');
INSERT INTO REGION (region_code, region_name) VALUES ('27200', '대구광역시 남구');
INSERT INTO REGION (region_code, region_name) VALUES ('27230', '대구광역시 북구');
INSERT INTO REGION (region_code, region_name) VALUES ('27260', '대구광역시 수성구');
INSERT INTO REGION (region_code, region_name) VALUES ('27290', '대구광역시 달서구');
INSERT INTO REGION (region_code, region_name) VALUES ('27710', '대구광역시 달성군');
INSERT INTO REGION (region_code, region_name) VALUES ('27720', '대구광역시 군위군');

-- 5. 인천광역시 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('28110', '인천광역시 중구');
INSERT INTO REGION (region_code, region_name) VALUES ('28140', '인천광역시 동구');
INSERT INTO REGION (region_code, region_name) VALUES ('28177', '인천광역시 미추홀구');
INSERT INTO REGION (region_code, region_name) VALUES ('28185', '인천광역시 연수구');
INSERT INTO REGION (region_code, region_name) VALUES ('28200', '인천광역시 남동구');
INSERT INTO REGION (region_code, region_name) VALUES ('28237', '인천광역시 부평구');
INSERT INTO REGION (region_code, region_name) VALUES ('28245', '인천광역시 계양구');
INSERT INTO REGION (region_code, region_name) VALUES ('28260', '인천광역시 서구');
INSERT INTO REGION (region_code, region_name) VALUES ('28710', '인천광역시 강화군');
INSERT INTO REGION (region_code, region_name) VALUES ('28720', '인천광역시 옹진군');

-- 6. 광주광역시 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('29110', '광주광역시 동구');
INSERT INTO REGION (region_code, region_name) VALUES ('29140', '광주광역시 서구');
INSERT INTO REGION (region_code, region_name) VALUES ('29155', '광주광역시 남구');
INSERT INTO REGION (region_code, region_name) VALUES ('29170', '광주광역시 북구');
INSERT INTO REGION (region_code, region_name) VALUES ('29200', '광주광역시 광산구');

-- 7. 대전광역시 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('30110', '대전광역시 동구');
INSERT INTO REGION (region_code, region_name) VALUES ('30140', '대전광역시 중구');
INSERT INTO REGION (region_code, region_name) VALUES ('30170', '대전광역시 서구');
INSERT INTO REGION (region_code, region_name) VALUES ('30200', '대전광역시 유성구');
INSERT INTO REGION (region_code, region_name) VALUES ('30230', '대전광역시 대덕구');

-- 8. 울산광역시 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('31110', '울산광역시 중구');
INSERT INTO REGION (region_code, region_name) VALUES ('31140', '울산광역시 남구');
INSERT INTO REGION (region_code, region_name) VALUES ('31170', '울산광역시 동구');
INSERT INTO REGION (region_code, region_name) VALUES ('31200', '울산광역시 북구');
INSERT INTO REGION (region_code, region_name) VALUES ('31710', '울산광역시 울주군');

-- 9. 경기도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('41110', '경기도 수원시');
INSERT INTO REGION (region_code, region_name) VALUES ('41130', '경기도 성남시');
INSERT INTO REGION (region_code, region_name) VALUES ('41150', '경기도 의정부시');
INSERT INTO REGION (region_code, region_name) VALUES ('41170', '경기도 안양시');
INSERT INTO REGION (region_code, region_name) VALUES ('41190', '경기도 부천시');
INSERT INTO REGION (region_code, region_name) VALUES ('41210', '경기도 광명시');
INSERT INTO REGION (region_code, region_name) VALUES ('41220', '경기도 평택시');
INSERT INTO REGION (region_code, region_name) VALUES ('41250', '경기도 동두천시');
INSERT INTO REGION (region_code, region_name) VALUES ('41270', '경기도 안산시');
INSERT INTO REGION (region_code, region_name) VALUES ('41280', '경기도 고양시');
INSERT INTO REGION (region_code, region_name) VALUES ('41290', '경기도 과천시');
INSERT INTO REGION (region_code, region_name) VALUES ('41310', '경기도 구리시');
INSERT INTO REGION (region_code, region_name) VALUES ('41360', '경기도 남양주시');
INSERT INTO REGION (region_code, region_name) VALUES ('41370', '경기도 오산시');
INSERT INTO REGION (region_code, region_name) VALUES ('41390', '경기도 시흥시');
INSERT INTO REGION (region_code, region_name) VALUES ('41410', '경기도 군포시');
INSERT INTO REGION (region_code, region_name) VALUES ('41430', '경기도 의왕시');
INSERT INTO REGION (region_code, region_name) VALUES ('41450', '경기도 하남시');
INSERT INTO REGION (region_code, region_name) VALUES ('41460', '경기도 용인시');
INSERT INTO REGION (region_code, region_name) VALUES ('41480', '경기도 파주시');
INSERT INTO REGION (region_code, region_name) VALUES ('41500', '경기도 이천시');
INSERT INTO REGION (region_code, region_name) VALUES ('41550', '경기도 안성시');
INSERT INTO REGION (region_code, region_name) VALUES ('41570', '경기도 김포시');
INSERT INTO REGION (region_code, region_name) VALUES ('41590', '경기도 화성시');
INSERT INTO REGION (region_code, region_name) VALUES ('41610', '경기도 광주시');
INSERT INTO REGION (region_code, region_name) VALUES ('41630', '경기도 양주시');
INSERT INTO REGION (region_code, region_name) VALUES ('41650', '경기도 포천시');
INSERT INTO REGION (region_code, region_name) VALUES ('41670', '경기도 여주시');
INSERT INTO REGION (region_code, region_name) VALUES ('41800', '경기도 연천군');
INSERT INTO REGION (region_code, region_name) VALUES ('41820', '경기도 가평군');
INSERT INTO REGION (region_code, region_name) VALUES ('41830', '경기도 양평군');

-- 10. 강원특별자치도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('42110', '강원특별자치도 춘천시');
INSERT INTO REGION (region_code, region_name) VALUES ('42130', '강원특별자치도 원주시');
INSERT INTO REGION (region_code, region_name) VALUES ('42150', '강원특별자치도 강릉시');
INSERT INTO REGION (region_code, region_name) VALUES ('42170', '강원특별자치도 동해시');
INSERT INTO REGION (region_code, region_name) VALUES ('42190', '강원특별자치도 태백시');
INSERT INTO REGION (region_code, region_name) VALUES ('42210', '강원특별자치도 속초시');
INSERT INTO REGION (region_code, region_name) VALUES ('42230', '강원특별자치도 삼척시');
INSERT INTO REGION (region_code, region_name) VALUES ('42720', '강원특별자치도 홍천군');
INSERT INTO REGION (region_code, region_name) VALUES ('42730', '강원특별자치도 횡성군');
INSERT INTO REGION (region_code, region_name) VALUES ('42750', '강원특별자치도 영월군');
INSERT INTO REGION (region_code, region_name) VALUES ('42760', '강원특별자치도 평창군');
INSERT INTO REGION (region_code, region_name) VALUES ('42770', '강원특별자치도 정선군');
INSERT INTO REGION (region_code, region_name) VALUES ('42780', '강원특별자치도 철원군');
INSERT INTO REGION (region_code, region_name) VALUES ('42790', '강원특별자치도 화천군');
INSERT INTO REGION (region_code, region_name) VALUES ('42800', '강원특별자치도 양구군');
INSERT INTO REGION (region_code, region_name) VALUES ('42810', '강원특별자치도 인제군');
INSERT INTO REGION (region_code, region_name) VALUES ('42820', '강원특별자치도 고성군');
INSERT INTO REGION (region_code, region_name) VALUES ('42830', '강원특별자치도 양양군');

-- 11. 충청북도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('43110', '충청북도 청주시');
INSERT INTO REGION (region_code, region_name) VALUES ('43130', '충청북도 충주시');
INSERT INTO REGION (region_code, region_name) VALUES ('43150', '충청북도 제천시');
INSERT INTO REGION (region_code, region_name) VALUES ('43720', '충청북도 보은군');
INSERT INTO REGION (region_code, region_name) VALUES ('43730', '충청북도 옥천군');
INSERT INTO REGION (region_code, region_name) VALUES ('43740', '충청북도 영동군');
INSERT INTO REGION (region_code, region_name) VALUES ('43745', '충청북도 증평군');
INSERT INTO REGION (region_code, region_name) VALUES ('43750', '충청북도 진천군');
INSERT INTO REGION (region_code, region_name) VALUES ('43760', '충청북도 괴산군');
INSERT INTO REGION (region_code, region_name) VALUES ('43770', '충청북도 음성군');
INSERT INTO REGION (region_code, region_name) VALUES ('43800', '충청북도 단양군');

-- 12. 충청남도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('44130', '충청남도 천안시');
INSERT INTO REGION (region_code, region_name) VALUES ('44150', '충청남도 공주시');
INSERT INTO REGION (region_code, region_name) VALUES ('44180', '충청남도 보령시');
INSERT INTO REGION (region_code, region_name) VALUES ('44200', '충청남도 아산시');
INSERT INTO REGION (region_code, region_name) VALUES ('44210', '충청남도 서산시');
INSERT INTO REGION (region_code, region_name) VALUES ('44230', '충청남도 논산시');
INSERT INTO REGION (region_code, region_name) VALUES ('44250', '충청남도 계룡시');
INSERT INTO REGION (region_code, region_name) VALUES ('44270', '충청남도 당진시');
INSERT INTO REGION (region_code, region_name) VALUES ('44710', '충청남도 금산군');
INSERT INTO REGION (region_code, region_name) VALUES ('44760', '충청남도 부여군');
INSERT INTO REGION (region_code, region_name) VALUES ('44770', '충청남도 서천군');
INSERT INTO REGION (region_code, region_name) VALUES ('44790', '충청남도 청양군');
INSERT INTO REGION (region_code, region_name) VALUES ('44800', '충청남도 홍성군');
INSERT INTO REGION (region_code, region_name) VALUES ('44810', '충청남도 예산군');
INSERT INTO REGION (region_code, region_name) VALUES ('44825', '충청남도 태안군');

-- 13. 전북특별자치도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('45110', '전북특별자치도 전주시');
INSERT INTO REGION (region_code, region_name) VALUES ('45130', '전북특별자치도 군산시');
INSERT INTO REGION (region_code, region_name) VALUES ('45140', '전북특별자치도 익산시');
INSERT INTO REGION (region_code, region_name) VALUES ('45180', '전북특별자치도 정읍시');
INSERT INTO REGION (region_code, region_name) VALUES ('45190', '전북특별자치도 남원시');
INSERT INTO REGION (region_code, region_name) VALUES ('45210', '전북특별자치도 김제시');
INSERT INTO REGION (region_code, region_name) VALUES ('45710', '전북특별자치도 완주군');
INSERT INTO REGION (region_code, region_name) VALUES ('45720', '전북특별자치도 진안군');
INSERT INTO REGION (region_code, region_name) VALUES ('45730', '전북특별자치도 무주군');
INSERT INTO REGION (region_code, region_name) VALUES ('45740', '전북특별자치도 장수군');
INSERT INTO REGION (region_code, region_name) VALUES ('45750', '전북특별자치도 임실군');
INSERT INTO REGION (region_code, region_name) VALUES ('45770', '전북특별자치도 순창군');
INSERT INTO REGION (region_code, region_name) VALUES ('45790', '전북특별자치도 고창군');
INSERT INTO REGION (region_code, region_name) VALUES ('45800', '전북특별자치도 부안군');

-- 14. 전라남도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('46110', '전라남도 목포시');
INSERT INTO REGION (region_code, region_name) VALUES ('46130', '전라남도 여수시');
INSERT INTO REGION (region_code, region_name) VALUES ('46150', '전라남도 순천시');
INSERT INTO REGION (region_code, region_name) VALUES ('46170', '전라남도 나주시');
INSERT INTO REGION (region_code, region_name) VALUES ('46230', '전라남도 광양시');
INSERT INTO REGION (region_code, region_name) VALUES ('46710', '전라남도 담양군');
INSERT INTO REGION (region_code, region_name) VALUES ('46720', '전라남도 곡성군');
INSERT INTO REGION (region_code, region_name) VALUES ('46730', '전라남도 구례군');
INSERT INTO REGION (region_code, region_name) VALUES ('46770', '전라남도 고흥군');
INSERT INTO REGION (region_code, region_name) VALUES ('46780', '전라남도 보성군');
INSERT INTO REGION (region_code, region_name) VALUES ('46790', '전라남도 화순군');
INSERT INTO REGION (region_code, region_name) VALUES ('46800', '전라남도 장흥군');
INSERT INTO REGION (region_code, region_name) VALUES ('46810', '전라남도 강진군');
INSERT INTO REGION (region_code, region_name) VALUES ('46820', '전라남도 해남군');
INSERT INTO REGION (region_code, region_name) VALUES ('46830', '전라남도 영암군');
INSERT INTO REGION (region_code, region_name) VALUES ('46840', '전라남도 무안군');
INSERT INTO REGION (region_code, region_name) VALUES ('46860', '전라남도 함평군');
INSERT INTO REGION (region_code, region_name) VALUES ('46870', '전라남도 영광군');
INSERT INTO REGION (region_code, region_name) VALUES ('46880', '전라남도 장성군');
INSERT INTO REGION (region_code, region_name) VALUES ('46890', '전라남도 완도군');
INSERT INTO REGION (region_code, region_name) VALUES ('46900', '전라남도 진도군');
INSERT INTO REGION (region_code, region_name) VALUES ('46910', '전라남도 신안군');

-- 15. 경상북도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('47110', '경상북도 포항시');
INSERT INTO REGION (region_code, region_name) VALUES ('47130', '경상북도 경주시');
INSERT INTO REGION (region_code, region_name) VALUES ('47150', '경상북도 김천시');
INSERT INTO REGION (region_code, region_name) VALUES ('47170', '경상북도 안동시');
INSERT INTO REGION (region_code, region_name) VALUES ('47190', '경상북도 구미시');
INSERT INTO REGION (region_code, region_name) VALUES ('47210', '경상북도 영주시');
INSERT INTO REGION (region_code, region_name) VALUES ('47230', '경상북도 영천시');
INSERT INTO REGION (region_code, region_name) VALUES ('47250', '경상북도 상주시');
INSERT INTO REGION (region_code, region_name) VALUES ('47280', '경상북도 문경시');
INSERT INTO REGION (region_code, region_name) VALUES ('47290', '경상북도 경산시');
INSERT INTO REGION (region_code, region_name) VALUES ('47720', '경상북도 군위군');
INSERT INTO REGION (region_code, region_name) VALUES ('47730', '경상북도 의성군');
INSERT INTO REGION (region_code, region_name) VALUES ('47750', '경상북도 청송군');
INSERT INTO REGION (region_code, region_name) VALUES ('47760', '경상북도 영양군');
INSERT INTO REGION (region_code, region_name) VALUES ('47770', '경상북도 영덕군');
INSERT INTO REGION (region_code, region_name) VALUES ('47820', '경상북도 청도군');
INSERT INTO REGION (region_code, region_name) VALUES ('47830', '경상북도 고령군');
INSERT INTO REGION (region_code, region_name) VALUES ('47840', '경상북도 성주군');
INSERT INTO REGION (region_code, region_name) VALUES ('47850', '경상북도 칠곡군');
INSERT INTO REGION (region_code, region_name) VALUES ('47900', '경상북도 예천군');
INSERT INTO REGION (region_code, region_name) VALUES ('47920', '경상북도 봉화군');
INSERT INTO REGION (region_code, region_name) VALUES ('47930', '경상북도 울진군');
INSERT INTO REGION (region_code, region_name) VALUES ('47940', '경상북도 울릉군');

-- 16. 경상남도 - 시군구
INSERT INTO REGION (region_code, region_name) VALUES ('48120', '경상남도 창원시');
INSERT INTO REGION (region_code, region_name) VALUES ('48170', '경상남도 진주시');
INSERT INTO REGION (region_code, region_name) VALUES ('48220', '경상남도 통영시');
INSERT INTO REGION (region_code, region_name) VALUES ('48240', '경상남도 사천시');
INSERT INTO REGION (region_code, region_name) VALUES ('48250', '경상남도 김해시');
INSERT INTO REGION (region_code, region_name) VALUES ('48270', '경상남도 밀양시');
INSERT INTO REGION (region_code, region_name) VALUES ('48310', '경상남도 거제시');
INSERT INTO REGION (region_code, region_name) VALUES ('48330', '경상남도 양산시');
INSERT INTO REGION (region_code, region_name) VALUES ('48720', '경상남도 의령군');
INSERT INTO REGION (region_code, region_name) VALUES ('48730', '경상남도 함안군');
INSERT INTO REGION (region_code, region_name) VALUES ('48740', '경상남도 창녕군');
INSERT INTO REGION (region_code, region_name) VALUES ('48820', '경상남도 고성군');
INSERT INTO REGION (region_code, region_name) VALUES ('48840', '경상남도 남해군');
INSERT INTO REGION (region_code, region_name) VALUES ('48850', '경상남도 하동군');
INSERT INTO REGION (region_code, region_name) VALUES ('48860', '경상남도 산청군');
INSERT INTO REGION (region_code, region_name) VALUES ('48870', '경상남도 함양군');
INSERT INTO REGION (region_code, region_name) VALUES ('48880', '경상남도 거창군');
INSERT INTO REGION (region_code, region_name) VALUES ('48890', '경상남도 합천군');

-- 17. 제주특별자치도 - 시
INSERT INTO REGION (region_code, region_name) VALUES ('50110', '제주특별자치도 제주시');
INSERT INTO REGION (region_code, region_name) VALUES ('50130', '제주특별자치도 서귀포시');

