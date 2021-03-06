package com.nemustech.sample.model;

import org.mybatisorm.annotation.Fields;
import org.mybatisorm.annotation.Join;

import com.nemustech.common.model.Default;
import com.nemustech.common.page.Paging;

/**
 * Sample 테이블을 기준으로 Test 테이블을 조인
 * 
 * @author skoh
 */
//@Join // Inner Join
//@Join("sample LEFT JOIN test") // Outer Join
@Join("sample LEFT JOIN test LEFT JOIN files2")
public class SampleAndTest extends Paging {
	@Fields("*")
	protected Sample sample = new Sample(); // 인스턴스를 생성해야 기본 조건이 만들어짐.

	@Fields("id, name") // @Column 이 선언된 필드명 리스트 (, 로 구분하고 모든 필드는 *)
	protected Test test = new Test();

	@Fields("*")
	protected Files2 files2 = new Files2();

	public SampleAndTest() {
	}

	public SampleAndTest(Sample sample, Test test, Files2 files2) {
		this.sample = sample;
		this.test = test;
		this.files2 = files2;
	}

	@Override
	public Default model() {
		return sample;
	}

	@Override
	public Default[] joinModels() {
		return new Default[] { test, files2 };
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
	}

	public Files2 getFiles2() {
		return files2;
	}

	public void setFiles(Files2 files2) {
		this.files2 = files2;
	}
}