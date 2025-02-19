package mapleglory.world;

import mapleglory.world.item.ItemConstants;
import mapleglory.world.item.ItemVariationOption;
import mapleglory.world.job.Job;
import mapleglory.world.job.JobConstants;
import mapleglory.world.user.stat.StatConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class ConstantsTest {
    @Test
    public void testSkillRootFromJob() {
        for (Job job : Job.values()) {
            final int jobId = job.getJobId();
            final List<Integer> skillRoots = JobConstants.getSkillRootFromJob(jobId);
            Assertions.assertNotEquals(0, skillRoots.size());
            for (int skillRoot : skillRoots) {
                Assertions.assertNotNull(Job.getById(skillRoot));
            }
        }
    }

    @Test
    public void testMinHpMp() {
        Assertions.assertEquals(50, StatConstants.getMinHp(1, Job.BEGINNER.getJobId()));
        Assertions.assertEquals(5, StatConstants.getMinMp(1, Job.BEGINNER.getJobId()));

        for (Job job : Job.values()) {
            if (job == Job.MANAGER || job == Job.GM || job == Job.SUPER_GM || job == Job.ADDITIONAL_SKILLS) {
                continue;
            }

            final int hp = StatConstants.getMinHp(120, job.getJobId());
            Assertions.assertTrue(hp > 0);

            final int mp = StatConstants.getMinMp(120, job.getJobId());
            Assertions.assertTrue(mp > 0);
        }
    }

    @Test
    public void testExpTable() {
        final int[] expected = new int[]{
                0, 15, 34, 57, 92, 135, 372, 560, 840, 1242, 1242, 1242, 1242, 1242, 1242, 1490, 1788, 2146, 2575, 3090, 3708, 4450, 5340, 6408, 7690, 9228, 11074, 13289, 15947, 19136, 19136, 19136, 19136, 19136, 19136, 22963, 27556, 33067, 39680, 47616, 51425, 55539, 59982, 64781, 69963, 75560, 81605, 88133, 95184, 102799, 111023, 119905, 129497, 139857, 151046, 163130, 176180, 190274, 205496, 221936, 239691, 258866, 279575, 301941, 326096, 352184, 380359, 410788, 443651, 479143, 479143, 479143, 479143, 479143, 479143, 512683, 548571, 586971, 628059, 672023, 719065, 769400, 823258, 880886, 942548, 1008526, 1079123, 1154662, 1235488, 1321972, 1414510, 1513526, 1619473, 1732836, 1854135, 1983924, 2122799, 2271395, 2430393, 2600521, 2782557, 2977336, 3185750, 3408753, 3647366, 3902682, 4175870, 4468181, 4780954, 5115621, 5473714, 5856874, 6266855, 6705535, 7174922, 7677167, 8214569, 8789589, 9404860, 10063200, 10063200, 10063200, 10063200, 10063200, 10063200, 10767624, 11521358, 12327853, 13190803, 14114159, 15102150, 16159301, 17290452, 18500784, 19795839, 21181548, 22664256, 24250754, 25948307, 27764688, 29708216, 31787791, 34012936, 36393842, 38941411, 41667310, 44584022, 47704904, 51044247, 54617344, 58440558, 62531397, 66908595, 71592197, 76603651, 81965907, 87703520, 93842766, 100411760, 107440583, 113887018, 120720239, 127963453, 135641260, 143779736, 152406520, 161550911, 171243966, 181518604, 192409720, 203954303, 216191561, 229163055, 242912838, 257487608, 272936864, 289313076, 306671861, 325072173, 344576503, 365251093, 387166159, 410396129, 435019897, 461121091, 488788356, 518115657, 549202596, 582154752, 617084037, 654109079, 693355624, 734956961, 779054379, 825797642, 875345501, 927866231, 983538205, 1042550497, 1105103527, 0
        };
        Assertions.assertArrayEquals(expected, GameConstants.EXP_TABLE);
    }

    @Test
    public void testJobLevel() {
        for (Job job : Job.values()) {
            if (job == Job.MANAGER || job == Job.GM || job == Job.SUPER_GM || job == Job.ADDITIONAL_SKILLS) {
                continue;
            }

            final int jobLevel = JobConstants.getJobLevel(job.getJobId());

            // System.out.printf("%s %d\n", job, jobLevel);
        }
    }

    @Test
    public void testItemVariation() {
        final int v = 10;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < 20000; i++) {
            final int r = ItemConstants.getVariation(v, ItemVariationOption.NORMAL);
            if (r - v > max) {
                max = r - v;
            }
            if (r - v < min) {
                min = r - v;
            }
        }
        System.out.printf("%d : %d ~ +%d\n", v, min, max);
    }
}
