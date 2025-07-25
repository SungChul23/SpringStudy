package com.kim.SpringStudy.controller;


import com.kim.SpringStudy.domain.KBO;
import com.kim.SpringStudy.domain.KBOTeam;
import com.kim.SpringStudy.repository.KBORepository;
import com.kim.SpringStudy.repository.KBOTeamRepository;
import com.kim.SpringStudy.service.KBOService;
import com.kim.SpringStudy.service.TeamNewsService;
import com.kim.SpringStudy.dto.NewsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DugoutController {

    private final KBOService kboService;
    private final KBOTeamRepository kboTeamRepository;
    private final TeamNewsService teamNewsService;


    //더그 아웃 입장
    @GetMapping("/dugout")
    public String Dugout() {
        return "myKBO";
    }

    //팀별 순위 그래프
    @GetMapping("/kbo/teamRank")
    public String showTeamRank(@RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                               LocalDate date,
                               Model model) {
        if (date == null) {
            List<LocalDate> dates = kboService.getAvailableDates();
            if (!dates.isEmpty()) date = dates.get(0); // 최신 날짜 선택
        }

        List<KBO> teams = kboService.getTeamRankByDate(date);
        List<LocalDate> allDates = kboService.getAvailableDates();

        model.addAttribute("teams", teams);
        model.addAttribute("dates", allDates);
        model.addAttribute("selectedDate", date);

        //그래프 그리기
        List<String> teamNames = teams.stream().map(KBO::getTeamName).toList();
        List<Double> winRates = teams.stream().map(KBO::getWinRate).toList();
        model.addAttribute("teamNames", teamNames);
        model.addAttribute("winRates", winRates);

        return "teamRank";
    }

    //구단 별 예매 링크
    @GetMapping("/tickets")
    public String Tickets(Model model) {
        List<KBOTeam> result = kboTeamRepository.findAll();
        model.addAttribute("topTeams", result.subList(0, 5));
        model.addAttribute("bottomTeams", result.subList(5, 10));
        return "tickets";
    }

    //HTML View 페이지를 보여주는 컨트롤러
    @GetMapping("/news/view/{team}")
    public String viewTeamNews(@PathVariable String team, Model model) {
        model.addAttribute("team", team); // Thymeleaf에서 JS로 넘겨줄 값
        return "newsView"; // → templates/newsView.html
    }
    @GetMapping("/news/{team}")
    @ResponseBody
    public List<NewsDTO> getTeamNews(@PathVariable String team,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "5") int display) {

        String query = switch (team.toUpperCase()) {
            case "SSG" -> "SSG 랜더스";
            case "LG" -> "LG 트윈스";
            case "DOOSAN" -> "두산 베어스";
            case "LOTTE" -> "롯데 자이언츠";
            case "NC" -> "NC 다이노스";
            case "KIA" -> "KIA 타이거즈";
            case "KIWOOM" -> "키움 히어로즈";
            case "HANWHA" -> "한화 이글스";
            case "KT" -> "KT 위즈";
            case "SAMSUNG" -> "삼성 라이온즈";
            default -> "KBO";
        };

        return teamNewsService.getNewsByTeam(query, page, display);
    }

}
