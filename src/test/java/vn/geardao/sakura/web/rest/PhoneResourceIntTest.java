package vn.geardao.sakura.web.rest;

import vn.geardao.sakura.SakuraApp;

import vn.geardao.sakura.domain.Phone;
import vn.geardao.sakura.repository.PhoneRepository;
import vn.geardao.sakura.service.PhoneService;
import vn.geardao.sakura.service.dto.PhoneDTO;
import vn.geardao.sakura.service.mapper.PhoneMapper;
import vn.geardao.sakura.web.rest.errors.ExceptionTranslator;
import vn.geardao.sakura.service.dto.PhoneCriteria;
import vn.geardao.sakura.service.PhoneQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


import static vn.geardao.sakura.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PhoneResource REST controller.
 *
 * @see PhoneResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SakuraApp.class)
public class PhoneResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final LocalDate DEFAULT_RELEASE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RELEASE_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private PhoneMapper phoneMapper;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private PhoneQueryService phoneQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPhoneMockMvc;

    private Phone phone;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PhoneResource phoneResource = new PhoneResource(phoneService, phoneQueryService);
        this.restPhoneMockMvc = MockMvcBuilders.standaloneSetup(phoneResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Phone createEntity(EntityManager em) {
        Phone phone = new Phone()
            .name(DEFAULT_NAME)
            .brand(DEFAULT_BRAND)
            .price(DEFAULT_PRICE)
            .releaseDate(DEFAULT_RELEASE_DATE);
        return phone;
    }

    @Before
    public void initTest() {
        phone = createEntity(em);
    }

    @Test
    @Transactional
    public void createPhone() throws Exception {
        int databaseSizeBeforeCreate = phoneRepository.findAll().size();

        // Create the Phone
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);
        restPhoneMockMvc.perform(post("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isCreated());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeCreate + 1);
        Phone testPhone = phoneList.get(phoneList.size() - 1);
        assertThat(testPhone.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPhone.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testPhone.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testPhone.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void createPhoneWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = phoneRepository.findAll().size();

        // Create the Phone with an existing ID
        phone.setId(1L);
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhoneMockMvc.perform(post("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = phoneRepository.findAll().size();
        // set the field null
        phone.setName(null);

        // Create the Phone, which fails.
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);

        restPhoneMockMvc.perform(post("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isBadRequest());

        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBrandIsRequired() throws Exception {
        int databaseSizeBeforeTest = phoneRepository.findAll().size();
        // set the field null
        phone.setBrand(null);

        // Create the Phone, which fails.
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);

        restPhoneMockMvc.perform(post("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isBadRequest());

        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPhones() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList
        restPhoneMockMvc.perform(get("/api/phones?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phone.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getPhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get the phone
        restPhoneMockMvc.perform(get("/api/phones/{id}", phone.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(phone.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllPhonesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where name equals to DEFAULT_NAME
        defaultPhoneShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the phoneList where name equals to UPDATED_NAME
        defaultPhoneShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPhonesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPhoneShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the phoneList where name equals to UPDATED_NAME
        defaultPhoneShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPhonesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where name is not null
        defaultPhoneShouldBeFound("name.specified=true");

        // Get all the phoneList where name is null
        defaultPhoneShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhonesByBrandIsEqualToSomething() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where brand equals to DEFAULT_BRAND
        defaultPhoneShouldBeFound("brand.equals=" + DEFAULT_BRAND);

        // Get all the phoneList where brand equals to UPDATED_BRAND
        defaultPhoneShouldNotBeFound("brand.equals=" + UPDATED_BRAND);
    }

    @Test
    @Transactional
    public void getAllPhonesByBrandIsInShouldWork() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where brand in DEFAULT_BRAND or UPDATED_BRAND
        defaultPhoneShouldBeFound("brand.in=" + DEFAULT_BRAND + "," + UPDATED_BRAND);

        // Get all the phoneList where brand equals to UPDATED_BRAND
        defaultPhoneShouldNotBeFound("brand.in=" + UPDATED_BRAND);
    }

    @Test
    @Transactional
    public void getAllPhonesByBrandIsNullOrNotNull() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where brand is not null
        defaultPhoneShouldBeFound("brand.specified=true");

        // Get all the phoneList where brand is null
        defaultPhoneShouldNotBeFound("brand.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhonesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where price equals to DEFAULT_PRICE
        defaultPhoneShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the phoneList where price equals to UPDATED_PRICE
        defaultPhoneShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPhonesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultPhoneShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the phoneList where price equals to UPDATED_PRICE
        defaultPhoneShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllPhonesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where price is not null
        defaultPhoneShouldBeFound("price.specified=true");

        // Get all the phoneList where price is null
        defaultPhoneShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhonesByReleaseDateIsEqualToSomething() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where releaseDate equals to DEFAULT_RELEASE_DATE
        defaultPhoneShouldBeFound("releaseDate.equals=" + DEFAULT_RELEASE_DATE);

        // Get all the phoneList where releaseDate equals to UPDATED_RELEASE_DATE
        defaultPhoneShouldNotBeFound("releaseDate.equals=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllPhonesByReleaseDateIsInShouldWork() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where releaseDate in DEFAULT_RELEASE_DATE or UPDATED_RELEASE_DATE
        defaultPhoneShouldBeFound("releaseDate.in=" + DEFAULT_RELEASE_DATE + "," + UPDATED_RELEASE_DATE);

        // Get all the phoneList where releaseDate equals to UPDATED_RELEASE_DATE
        defaultPhoneShouldNotBeFound("releaseDate.in=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllPhonesByReleaseDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where releaseDate is not null
        defaultPhoneShouldBeFound("releaseDate.specified=true");

        // Get all the phoneList where releaseDate is null
        defaultPhoneShouldNotBeFound("releaseDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPhonesByReleaseDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where releaseDate greater than or equals to DEFAULT_RELEASE_DATE
        defaultPhoneShouldBeFound("releaseDate.greaterOrEqualThan=" + DEFAULT_RELEASE_DATE);

        // Get all the phoneList where releaseDate greater than or equals to UPDATED_RELEASE_DATE
        defaultPhoneShouldNotBeFound("releaseDate.greaterOrEqualThan=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllPhonesByReleaseDateIsLessThanSomething() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        // Get all the phoneList where releaseDate less than or equals to DEFAULT_RELEASE_DATE
        defaultPhoneShouldNotBeFound("releaseDate.lessThan=" + DEFAULT_RELEASE_DATE);

        // Get all the phoneList where releaseDate less than or equals to UPDATED_RELEASE_DATE
        defaultPhoneShouldBeFound("releaseDate.lessThan=" + UPDATED_RELEASE_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPhoneShouldBeFound(String filter) throws Exception {
        restPhoneMockMvc.perform(get("/api/phones?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phone.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())));

        // Check, that the count call also returns 1
        restPhoneMockMvc.perform(get("/api/phones/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPhoneShouldNotBeFound(String filter) throws Exception {
        restPhoneMockMvc.perform(get("/api/phones?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPhoneMockMvc.perform(get("/api/phones/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPhone() throws Exception {
        // Get the phone
        restPhoneMockMvc.perform(get("/api/phones/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        int databaseSizeBeforeUpdate = phoneRepository.findAll().size();

        // Update the phone
        Phone updatedPhone = phoneRepository.findById(phone.getId()).get();
        // Disconnect from session so that the updates on updatedPhone are not directly saved in db
        em.detach(updatedPhone);
        updatedPhone
            .name(UPDATED_NAME)
            .brand(UPDATED_BRAND)
            .price(UPDATED_PRICE)
            .releaseDate(UPDATED_RELEASE_DATE);
        PhoneDTO phoneDTO = phoneMapper.toDto(updatedPhone);

        restPhoneMockMvc.perform(put("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isOk());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeUpdate);
        Phone testPhone = phoneList.get(phoneList.size() - 1);
        assertThat(testPhone.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPhone.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testPhone.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPhone.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingPhone() throws Exception {
        int databaseSizeBeforeUpdate = phoneRepository.findAll().size();

        // Create the Phone
        PhoneDTO phoneDTO = phoneMapper.toDto(phone);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhoneMockMvc.perform(put("/api/phones")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(phoneDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Phone in the database
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePhone() throws Exception {
        // Initialize the database
        phoneRepository.saveAndFlush(phone);

        int databaseSizeBeforeDelete = phoneRepository.findAll().size();

        // Get the phone
        restPhoneMockMvc.perform(delete("/api/phones/{id}", phone.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Phone> phoneList = phoneRepository.findAll();
        assertThat(phoneList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Phone.class);
        Phone phone1 = new Phone();
        phone1.setId(1L);
        Phone phone2 = new Phone();
        phone2.setId(phone1.getId());
        assertThat(phone1).isEqualTo(phone2);
        phone2.setId(2L);
        assertThat(phone1).isNotEqualTo(phone2);
        phone1.setId(null);
        assertThat(phone1).isNotEqualTo(phone2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhoneDTO.class);
        PhoneDTO phoneDTO1 = new PhoneDTO();
        phoneDTO1.setId(1L);
        PhoneDTO phoneDTO2 = new PhoneDTO();
        assertThat(phoneDTO1).isNotEqualTo(phoneDTO2);
        phoneDTO2.setId(phoneDTO1.getId());
        assertThat(phoneDTO1).isEqualTo(phoneDTO2);
        phoneDTO2.setId(2L);
        assertThat(phoneDTO1).isNotEqualTo(phoneDTO2);
        phoneDTO1.setId(null);
        assertThat(phoneDTO1).isNotEqualTo(phoneDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(phoneMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(phoneMapper.fromId(null)).isNull();
    }
}
