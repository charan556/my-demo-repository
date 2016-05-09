package demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import demo.domain.Word;

@FeignClient("NOUN")
public interface NounClient {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Word getWord();

	static class HystrixClientFallback implements NounClient {

		@Autowired
		LoadBalancerClient loadBalancer;

		@Override
		public Word getWord() {
			ServiceInstance instance = loadBalancer.choose("NOUN");
			String word = (new RestTemplate()).getForObject(instance.getUri(), String.class);
			return new Word(word);
		}

	}

}
