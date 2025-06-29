package priv.common;

import java.awt.*;

public class Common {
    public static final int interfaceStartX = 10;
    public static final int startX = interfaceStartX - 1;

    public static final int interfaceStartY = 10;
    public static final int startY = interfaceStartY - 1;

    public static final int FUTURE_BODY_FIRE_NUMBER_TOP = 2001;//未来body中，火的number 往上的火
    public static final int FUTURE_BODY_FIRE_NUMBER_BOTTOM = 2002;//往下的火
    public static final int FUTURE_BODY_FIRE_NUMBER_LEFT = 2003;//往左的火
    public static final int FUTURE_BODY_FIRE_NUMBER_RIGHT = 2004;//往右的火
    public static final int FUTURE_BODY_FIRE_NUMBER_CENTRE = 2005;//中心火

    public static final int PROP_SCOPE_ADD = 3001; //范围提升
    public static final int PROP_BOM_ADD = 3002; //炸弹数量提升
    public static final int PROP_BOM_CONTROL = 3003; //炸弹爆炸控制
    public static final int PROP_SPEED_ADD = 3004; //速度提升
    public static final int PROP_BOM_THROUGH = 3005; //炸弹穿越
    public static final int PROP_WALL_THROUGH = 3006; //墙壁穿越
    public static final int PROP_QUESTION_MARK = 3007; //短暂无敌
    public static final int PROP_FIRE_IMMUNE = 3008; //火焰免疫
    public static final int PROP_DOOR = 3009; //门
    public static final int PROP_TANK = 3010; //坦克

    public static final Image first_stage_wall;
    public static final Image second_stage_wall;
    public static final Image third_stage_wall;
    public static final Image not_destroy_wall;

    public static final Image first_stage_bom;
    public static final Image second_stage_bom;
    public static final Image third_stage_bom;
    public static final Image fire1;
    public static final Image fire2;
    public static final Image fire3;

    public static final Image basicsCritter;
    public static final Image eliteCritter;
    public static final Image knightCritter1;
    public static final Image knightCritter2;
    public static final Image dragonCritter1;
    public static final Image dragonCritter2;

    public static final Image prop;
    public static final Image door;

    public static final Image gameOver;

    public static final Image player;
    public static final Image player_question_mark;
    public static final Image player_path;
    public static final Image player_tank;
    public static final Image missile;

    public static final Image charmander;

    private static final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
    static {
        Class<Common> commonClass = Common.class;
        not_destroy_wall = defaultToolkit.getImage(commonClass.getResource("/wall/not_destroy_wall.png"));
        first_stage_wall = defaultToolkit.getImage(commonClass.getResource("/wall/first_stage_wall.png"));
        second_stage_wall = defaultToolkit.getImage(commonClass.getResource("/wall/second_stage_wall.png"));
        third_stage_wall = defaultToolkit.getImage(commonClass.getResource("/wall/third_stage_wall.png"));
        fire1 = defaultToolkit.getImage(commonClass.getResource("/bom/1_fire.png"));
        fire2 = defaultToolkit.getImage(commonClass.getResource("/bom/2_fire.png"));
        fire3 = defaultToolkit.getImage(commonClass.getResource("/bom/3_fire.png"));
        first_stage_bom = defaultToolkit.getImage(commonClass.getResource("/bom/first_stage_bom.png"));
        second_stage_bom = defaultToolkit.getImage(commonClass.getResource("/bom/second_stage_bom.png"));
        third_stage_bom = defaultToolkit.getImage(commonClass.getResource("/bom/third_stage_bom.png"));

        basicsCritter = defaultToolkit.getImage(commonClass.getResource("/critter/basics/basics_critter.png"));
        eliteCritter = defaultToolkit.getImage(commonClass.getResource("/critter/elite/elite_critter.png"));
        knightCritter1 = defaultToolkit.getImage(commonClass.getResource("/critter/knight/knight_critter_1.png"));
        knightCritter2 = defaultToolkit.getImage(commonClass.getResource("/critter/knight/knight_critter_2.png"));
        dragonCritter1 = defaultToolkit.getImage(commonClass.getResource("/critter/dragon/dragon_critter_1.png"));
        dragonCritter2 = defaultToolkit.getImage(commonClass.getResource("/critter/dragon/dragon_critter_2.png"));

        prop = defaultToolkit.getImage(commonClass.getResource("/prop/prop.png"));
        door = defaultToolkit.getImage(commonClass.getResource("/prop/door.png"));

        gameOver = defaultToolkit.getImage(commonClass.getResource("/game_over.png"));

        player = defaultToolkit.getImage(commonClass.getResource("/player/player.png"));
        player_question_mark = defaultToolkit.getImage(commonClass.getResource("/player/player_question_mark.png"));
        player_path = defaultToolkit.getImage(commonClass.getResource("/player/path.png"));
        player_tank = defaultToolkit.getImage(commonClass.getResource("/tank/tank.png"));
        missile = defaultToolkit.getImage(commonClass.getResource("/tank/missile/missile.png"));

        charmander = defaultToolkit.getImage(commonClass.getResource("/charmander/charmander.png"));
    }

}
